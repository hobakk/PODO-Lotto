package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final LottoRepository lottoRepository;
	private final RedisDao redisDao;
	private final Manager manager;

	// 보안관련 더 생각해봐야함
	public ApiResponse setAdmin(OnlyMsgRequest request, User user, Long userId) {
		String KEY = "AdminSecurityKey";
		if (!request.getMsg().equals(KEY)) throw new IllegalArgumentException("설정된 KEY값이 아닙니다");

		User target = confirmationProcess(user, userId);
		target.setAdmin();
		return ApiResponse.ok("변경 완료");
	}

	// page 처리 필요함
	public ListApiResponse<UsersReponse> getUsers() {
		return ListApiResponse.ok("조회 성공", userRepository.findAll().stream().map(UsersReponse::new).collect(Collectors.toList()));
	}

	public ListApiResponse<AdminGetChargingResponse> getCharges() {
		List<String> valueList = redisDao.multiGet("All");

		List<AdminGetChargingResponse> userChargesValues = valueList.stream()
			.map(AdminGetChargingResponse::new).toList();
		return ListApiResponse.ok("조회 성공", userChargesValues);
	}

	public ItemApiResponse<AdminGetChargingResponse> searchCharging(String msg, int cash) {
		String searchStr = msg + "-" + cash;
		List<String> value = redisDao.multiGet(searchStr);
		AdminGetChargingResponse response = new AdminGetChargingResponse(value.get(0));
		return ItemApiResponse.ok("조회 성공", response);
	}

	// 결제에 대해서 고민해봐야함 현재 로직은 특정 계좌에 msg 와 value 가 확인되면 수동으로 넣어주는 방식
	public ApiResponse upCash(CashRequest cashRequest) {
		User user = manager.findUser(cashRequest.getUserId());
		String key = cashRequest.getUserId() + "-" + cashRequest.getMsg() + "-" + cashRequest.getValue();
		// searchCharging 에서 검증되어 넘어온 Request 이기에 값이 있는지에 대한 체크는 건너뛰어도 된다 생각함
		redisDao.deleteValues(key);

		user.setStatement(LocalDate.now() + "," + cashRequest.getValue() +"원 충전");
		user.setCash("+", cashRequest.getValue());
		user.setChargingCount(0);
		return ApiResponse.ok("충전 완료");
	}

	public ApiResponse downCash(CashRequest cashRequest) {
		User user = manager.findUser(cashRequest.getUserId());
		if (user.getCash() < cashRequest.getValue()) {
			throw new IllegalArgumentException("해당 유저가 보유한 금액보다 많습니다");
		}

		user.setCash("-", cashRequest.getValue());
		user.setStatement(LocalDate.now() + "," + cashRequest.getMsg() + ": " + cashRequest.getValue() + "원 차감");
		return ApiResponse.ok("차감 완료");
	}

	//초기 로또메인 만들기 위한 코드, 이후 사용할 일이 적어서 코드 중복사용을 안해서 생기는 불이익이 없을거라 생각
	public ApiResponse createLotto(String email) {
		Optional<Lotto> findMain = lottoRepository.findByMain();

		if (findMain.isPresent()) throw new IllegalArgumentException("메인 로또가 이미 생성되어 있습니다");

		List<Integer> countList = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			countList.add(1);
		}
		Lotto lotto = new Lotto("main", email, null, countList,  "");
		lottoRepository.save(lotto);
		return ApiResponse.ok("생성 완료");
	}

	public ApiResponse setStatus(User user, Long userId, OnlyMsgRequest request) {
		User target = confirmationProcess(user, userId);
		List<String> statusList = Arrays.asList("ACTIVE", "SUSPENDED", "DORMANT");

		if (!statusList.contains(request.getMsg())) throw new CustomException(INVALID_INPUT);

		String targetStatusStr = target.getStatus().toString();

		if (targetStatusStr.equals(request.getMsg())) throw new IllegalArgumentException("이미 적용되어 있는 상태코드 입니다");

		target.setStatus(request.getMsg());

		if (target.getStatus().equals(Status.SUSPENDED) || target.getStatus().equals(Status.DORMANT)) {
			redisDao.deleteInRedisValueIsNotNull(target.getId());
		}
		return ApiResponse.ok("상태 변경 완료");
	}

	public ApiResponse setRole(User user, Long userId, OnlyMsgRequest request) {
		User target = confirmationProcess(user, userId);

		UserRole changeRole = null;
		switch (request.getMsg()) {
			case "USER" -> changeRole = UserRole.ROLE_USER;
			case "PAID" -> changeRole = UserRole.ROLE_PAID;
		}

		if (target.getRole().equals(changeRole)) throw new IllegalArgumentException("동일한 권한입니다");

		target.setRole(changeRole);
		return ApiResponse.ok("권한 변경 완료");
	}

	private User confirmationProcess(User user, Long userId) {
		if (user.getId().equals(userId)) {
			throw new IllegalArgumentException("본인 입니다");
		}

		User target = manager.findUser(userId);
		if (target.getRole().equals(UserRole.ROLE_ADMIN)) {
			throw new IllegalArgumentException("운영자 계정입니다");
		}
		return target;
	}
}
