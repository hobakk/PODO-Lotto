package com.example.sixnumber.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
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

	private final CashRepository cashRepository;
	private final UserRepository userRepository;
	private final LottoRepository lottoRepository;
	private final RedisTemplate<String, String> redisTemplate;

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

	public ListApiResponse<Cash> getAfterChargs() {
		return ListApiResponse.ok("조회 성공", cashRepository.processingEqaulAfter());
	}
	public ListApiResponse<Cash> getBeforeChargs() {
		return ListApiResponse.ok("조회 성공", cashRepository.processingEqaulBefore());
	}

	public ApiResponse upCash(CashRequest cashRequest) {
		User user = findByUser(cashRequest.getUserId());
		Cash cash = cashRepository.findById(cashRequest.getCashId())
			.orElseThrow(() -> new IllegalArgumentException("해당 정보가 존재하지 않습니다"));

		if (!user.getId().equals(cash.getUserId())) {
			throw new IllegalArgumentException("충전 요청한 사용자 정보와 동일하지 않습니다");
		}
		user.setCash("+", cashRequest.getValue());
		cash.setProcessingAfter();
		return ApiResponse.ok("충전 완료");
	}

	public ApiResponse downCash(CashRequest cashRequest) {
		User user = findByUser(cashRequest.getUserId());
		if (user.getCash() < cashRequest.getValue()) {
			throw new IllegalArgumentException("해당 유저가 보유한 금액보다 많습니다");
		}
		user.setCash("-", cashRequest.getValue());
		return ApiResponse.ok("차감 완료");
	}

	//초기 로또메인 만들기 위한 코드, 이후 사용할 일이 적어서 코드 중복사용을 안해서 생기는 불이익이 없을거라 생각
	public ApiResponse createLotto(String email) {
		Optional<Lotto> findMain = lottoRepository.findByMain();

		if (findMain.isPresent()) {
			throw new IllegalArgumentException("메인 로또가 이미 생성되어 있습니다");
		}

		List<Integer> countList = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			countList.add(1);
		}
		Lotto lotto = new Lotto("main", email, null, countList, "", "");
		lottoRepository.save(lotto);
		return ApiResponse.ok("생성 완료");
	}

	public ApiResponse setStatus(User user, Long userId, OnlyMsgRequest request) {
		User target = confirmationProcess(user, userId);
		String[] statusStr = {"ACTIVE", "SUSPENDED", "DORMANT"};
		List<String> statusList = Arrays.asList(statusStr);

		if (!statusList.contains(request.getMsg())) throw new IllegalArgumentException("잘못된 입력값입니다");

		String targetStatusStr = target.getStatus().toString();

		if (targetStatusStr.equals(request.getMsg())) throw new IllegalArgumentException("이미 적용되어 있는 상태코드 입니다");

		target.setStatus(request.getMsg());

		if (target.getStatus().equals(Status.SUSPENDED) || target.getStatus().equals(Status.DORMANT)) {
			if (redisTemplate.opsForValue().get("RT: " + target.getId()) != null) {
				redisTemplate.delete("RT: " + target.getId());
			}
		}
		return ApiResponse.ok("상태 변경 완료");
	}

	private User findByUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}

	private User confirmationProcess(User user, Long userId) {
		if (user.getId().equals(userId)) {
			throw new IllegalArgumentException("본인 입니다");
		}

		User target = findByUser(userId);
		if (target.getRole().equals(UserRole.ROLE_ADMIN)) {
			throw new IllegalArgumentException("운영자 계정입니다");
		}
		return target;
	}
}
