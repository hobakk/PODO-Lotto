package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.example.sixnumber.user.entity.Statement;
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
	private final LottoRepository lottoRepository;
	private final RedisDao redisDao;
	private final Manager manager;

	// 보안관련 더 생각해봐야함
	public UnifiedResponse<?> setAdmin(OnlyMsgRequest request, User user, Long userId) {
		if (!request.getMsg().equals(KEY)) throw new IllegalArgumentException("설정된 Key 값이 아닙니다");

		User target = confirmationProcess(user, userId);
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
		List<String> valueList = redisDao.multiGet(RedisDao.CHARGE_KEY);

		List<AdminGetChargingResponse> userChargesValues = valueList.stream()
			.map(AdminGetChargingResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("조회 성공", userChargesValues);
	}

	public UnifiedResponse<AdminGetChargingResponse> searchCharging(String msg, int cash) {
		String searchStr = msg + "-" + cash;
		AdminGetChargingResponse response = redisDao.multiGet(searchStr).stream()
			.findFirst()
			.map(AdminGetChargingResponse::new)
			.orElseThrow(() -> new CustomException(NOT_FOUND));

		return UnifiedResponse.ok("조회 성공", response);
	}

	public UnifiedResponse<?> upCash(CashRequest cashRequest) {
		User user = manager.findUser(cashRequest.getUserId());
		String key = String.format("%d-%s-%d",
			cashRequest.getUserId(), cashRequest.getMsg(), cashRequest.getCash());
		redisDao.delete(RedisDao.CHARGE_KEY + key);

		user.addStatement(new Statement(user, "충전", cashRequest.getCash()));
		user.plusCash(cashRequest.getCash());
		user.setTimeOutCount(0);
		return UnifiedResponse.ok("충전 완료");
	}

	public UnifiedResponse<?> downCash(CashRequest cashRequest) {
		User user = manager.findUser(cashRequest.getUserId());
		if (user.getCash() < cashRequest.getCash()) {
			throw new IllegalArgumentException("해당 유저가 보유한 금액보다 많습니다");
		}

		user.minusCash(cashRequest.getCash());
		user.addStatement(new Statement(user, "차감", cashRequest.getCash(), "관리자에게 문의하세요"));
		return UnifiedResponse.ok("차감 완료");
	}

	public UnifiedResponse<?> createLotto(String email) {
		Optional<Lotto> findMain = lottoRepository.findByMain();
		findMain.ifPresentOrElse(
			main -> { throw new IllegalArgumentException("메인 로또가 이미 생성되어 있습니다"); },
			() -> {
				List<Integer> countList = new ArrayList<>(Collections.nCopies(45, 1));
				Lotto lotto = new Lotto("main", email, countList);
				lottoRepository.save(lotto);
			}
		);

		return UnifiedResponse.ok("생성 완료");
	}

	public UnifiedResponse<?> setStatus(User user, Long targetId, OnlyMsgRequest request) {
		User target = confirmationProcess(user, targetId);
		Status status;
		switch (request.getMsg()) {
			case "ACTIVE": status = Status.ACTIVE; break;
			case "SUSPENDED": status = Status.SUSPENDED; break;
			case "DORMANT": status = Status.DORMANT; break;
			default: throw new CustomException(INVALID_INPUT);
		}

		if (target.getStatus().equals(status)) throw new IllegalArgumentException("이미 적용되어 있는 상태코드 입니다");

		target.setStatus(status);
		if (target.getStatus().equals(Status.SUSPENDED) || target.getStatus().equals(Status.DORMANT)) {
			redisDao.delete(RedisDao.RT_KEY + target.getRefreshPointer());
		}

		return UnifiedResponse.ok("상태 변경 완료");
	}

	public UnifiedResponse<?> setRole(User user, Long targetId, OnlyMsgRequest request) {
		User target = confirmationProcess(user, targetId);
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

	private User confirmationProcess(User user, Long targetId) {
		if (user.getId().equals(targetId)) throw new IllegalArgumentException("본인 입니다");

		User target = manager.findUser(targetId);
		if (target.getRole().equals(UserRole.ROLE_ADMIN)) throw new IllegalArgumentException("운영자 계정입니다");

		return target;
	}
}
