package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.util.*;
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

	private final UserRepository userRepository;
	private final RedisDao redisDao;

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

	public UnifiedResponse<?> setStatus(Long targetId, OnlyMsgRequest request) {
		Map<String, Status> statusMap = new HashMap<>();
		statusMap.put("ACTIVE", Status.ACTIVE);
		statusMap.put("SUSPENDED", Status.SUSPENDED);
		statusMap.put("DORMANT", Status.DORMANT);

		Status changeToStatus = statusMap.get(request.getMsg());
		User target = userRepository.findByIdAndRoleNotAndStatusNot(targetId, UserRole.ROLE_ADMIN, changeToStatus)
				.map(user -> {
					user.setStatus(changeToStatus);
					if (Arrays.asList(Status.SUSPENDED, Status.DORMANT).contains(user.getStatus())) {
						redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
					}

					return user;
				})
				.orElseThrow(() -> new IllegalArgumentException("관리자 계정이거나 동일한 상태코드 입니다"));
		userRepository.save(target);
		return UnifiedResponse.ok("상태 변경 완료");
	}

	public UnifiedResponse<?> setRole(Long targetId, OnlyMsgRequest request) {
		Map<String, UserRole> roleMap = new HashMap<>();
		roleMap.put("USER", UserRole.ROLE_USER);
		roleMap.put("PAID", UserRole.ROLE_PAID);

		UserRole changeRole = roleMap.get(request.getMsg());
		User target = userRepository.findByIdAndRoleNot(targetId, UserRole.ROLE_ADMIN)
				.filter(u -> !changeRole.equals(u.getRole()))
				.map(u -> u.setRole(changeRole))
				.orElseThrow(() -> new IllegalArgumentException("관리자 계정이거나 동일한 상태라 변경할 수 없습니다"));
		userRepository.save(target);
		return UnifiedResponse.ok("권한 변경 완료");
	}
}
