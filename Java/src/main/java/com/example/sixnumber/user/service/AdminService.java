package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

	@Value("${spring.admin.set-admin-key}")
	private String KEY;
	private final UserRepository userRepository;
	private final RedisDao redisDao;

	public UnifiedResponse<?> setAdmin(OnlyMsgRequest request, User user, Long userId) {
		if (!request.getMsg().equals(KEY)) throw new IllegalArgumentException("설정된 Key 값이 아닙니다");

		User target = getTargetForConfirmation(user, userId);
		target.setAdmin();
		return UnifiedResponse.ok("변경 완료");
	}

	// page 처리 필요함
	public UnifiedResponse<List<UserResponse>> getUsers() {
		List<UserResponse> userAllList = userRepository.findAll().stream()
			.map(UserResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("조회 성공", userAllList);
	}

	public UnifiedResponse<List<AdminGetChargingResponse>> getCharges() {
		List<AdminGetChargingResponse> userChargesValues = redisDao.multiGet(RedisDao.CHARGE_KEY).stream()
			.map(AdminGetChargingResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("조회 성공", userChargesValues);
	}

	public UnifiedResponse<AdminGetChargingResponse> searchCharging(String msg, int cash) {
		String searchStr = msg + "-" + cash;
		return redisDao.multiGet(searchStr).stream()
			.findFirst()
			.map(value -> {
				AdminGetChargingResponse response = new AdminGetChargingResponse(value);
				return UnifiedResponse.ok("조회 성공", response);
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
	}

	public UnifiedResponse<?> upCash(CashRequest cashRequest) {
		return userRepository.findById(cashRequest.getUserId())
			.map(user -> {
				String key = String.format("%d-%s-%d",
					cashRequest.getUserId(), cashRequest.getMsg(), cashRequest.getCash());
				redisDao.delete(RedisDao.CHARGE_KEY + key);

				user.depositProcessing(cashRequest.getCash());
				return UnifiedResponse.ok("충전 완료");
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
	}

	public UnifiedResponse<?> downCash(CashRequest cashRequest) {
		return userRepository.findByIdAndCashGreaterThanEqual(cashRequest.getUserId(), cashRequest.getCash())
			.map(user -> {
				user.withdrawalProcessing(cashRequest.getCash());
				return UnifiedResponse.ok("차감 완료");
			})
			.orElseThrow(() -> {
				String msg = "존재하지 않는 유저 또는 보유 금액보다 적은 유저입니다";
				return new IllegalArgumentException(msg);
			});
	}

	public UnifiedResponse<?> setStatus(User user, Long targetId, OnlyMsgRequest request) {
		User target = getTargetForConfirmation(user, targetId);

		Status changeToStatus;
		switch (request.getMsg()) {
			case "ACTIVE": changeToStatus = Status.ACTIVE; break;
			case "SUSPENDED": changeToStatus = Status.SUSPENDED; break;
			case "DORMANT": changeToStatus = Status.DORMANT; break;
			default: throw new CustomException(INVALID_INPUT);
		}

		if (target.getStatus().equals(changeToStatus))
			throw new IllegalArgumentException("이미 적용되어 있는 상태코드 입니다");

		target.setStatus(changeToStatus);
		Stream.of(Status.SUSPENDED, Status.DORMANT)
			.filter(status -> status == changeToStatus)
			.findFirst()
			.ifPresent(status -> redisDao.delete(RedisDao.RT_KEY + target.getRefreshPointer()));
		return UnifiedResponse.ok("상태 변경 완료");
	}

	public UnifiedResponse<?> setRole(User user, Long targetId, OnlyMsgRequest request) {
		User target = getTargetForConfirmation(user, targetId);

		UserRole changeRole;
		switch (request.getMsg()) {
			case "USER": changeRole = UserRole.ROLE_USER; break;
			case "PAID": changeRole = UserRole.ROLE_PAID; break;
			default: throw new CustomException(INVALID_INPUT);
		}

		if (target.getRole().equals(changeRole)) throw new IllegalArgumentException("동일한 권한입니다");

		target.setRole(changeRole);
		return UnifiedResponse.ok("권한 변경 완료");
	}

	private User getTargetForConfirmation(User user, Long targetId) {
		if (user.getId().equals(targetId)) throw new IllegalArgumentException("본인 입니다");

		return userRepository.findByIdAndRoleNot(targetId, UserRole.ROLE_ADMIN)
			.orElseThrow(() -> new IllegalArgumentException("관리자 계정입니다"));
	}
}
