package com.example.sixnumber.user.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.exception.BreakTheRulesException;
import com.example.sixnumber.global.exception.UserNotFoundException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.exception.OverlapException;
import com.example.sixnumber.user.exception.StatusNotActiveException;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, String> redisTemplate;
	private final String RTK = "RT: ";
	private final String STMT = "STMT: ";

	public ApiResponse signUp(SignupRequest request) {
		Optional<User> dormantUser = userRepository.findByStatusAndEmail(Status.DORMANT, request.getEmail());
		if (dormantUser.isPresent()) {
			User user = dormantUser.get();
			if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
				user.setStatus("ACTIVE");
				user.setWithdrawExpiration(null);
				userRepository.save(user);
				return ApiResponse.ok("재가입 완료");
			}
		}

		if (userRepository.existsUserByEmail(request.getEmail())) {
			throw new OverlapException("중복된 이메일입니다");
		}
		if (userRepository.existsUserByNickname(request.getNickname())) {
			throw new OverlapException("중복된 닉네임입니다");
		}

		String password = passwordEncoder.encode(request.getPassword());
		User user = new User(request, password);
		user.setStatement(LocalDate.now() + "," + "회원가입 기념 1000원 증정");
		userRepository.save(user);
		return ApiResponse.create("회원가입 완료");
	}

	public String signIn(SigninRequest request) {
		User user = findByUser(request.getEmail());

		if (redisTemplate.opsForValue().get(RTK + user.getId()) != null) {
			redisTemplate.delete(RTK + user.getId());
			throw new OverlapException("중복된 로그인입니다");
		}

		if (!user.getStatus().equals(Status.ACTIVE)) {
			String msg = "";
			switch (user.getStatus()) {
				case SUSPENDED -> msg = "정지된 계정입니다";
				case DORMANT -> msg = "탈퇴한 계정입니다";
				default -> msg = "잘못된 상태정보입니다";
			} throw new StatusNotActiveException(msg);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다");
		}

		String refreshToken = jwtProvider.refreshToken(user.getEmail(), user.getId());
		redisTemplate.opsForValue().set(RTK + user.getId(), refreshToken);

		return jwtProvider.accessToken(user.getEmail(), user.getId());
	}

	public ApiResponse logout(User user) {
		redisTemplate.delete(RTK + user.getId());
		return ApiResponse.ok("로그아웃 성공");
	}

	public ApiResponse withdraw(OnlyMsgRequest request, String email) {
		String withdrawMsg = "회원탈퇴";

		if (!request.getMsg().equals(withdrawMsg)) {
			throw new IllegalArgumentException("잘못된 문자열 입력");
		}
		User user = findByUser(email);
		user.setStatus("DORMANT");
		user.setWithdrawExpiration(LocalDate.now().plusMonths(1));
		redisTemplate.delete(RTK + user.getId());
		return ApiResponse.ok("회원 탈퇴 완료");
	}

	public ApiResponse setPaid(OnlyMsgRequest request, String email) {
		User user = findByUser(email);

		if (request.getMsg().equals("월정액 해지")) {
			if (!user.getRole().equals(UserRole.ROLE_PAID)) {
				throw new IllegalArgumentException("월정액 사용자가 아닙니다");
			}
			user.setPaymentDate(request.getMsg());
			return ApiResponse.ok("해지 신청 성공");
		}

		if (user.getCash() < 5000 || user.getRole().equals(UserRole.ROLE_PAID)) {
			throw new IllegalArgumentException("금액이 부족하거나 이미 월정액 이용자입니다");
		}
		user.setCash("-", 5000);
		user.setRole("PAID");
		user.setPaymentDate(YearMonth.now().toString());
		user.setStatement(LocalDate.now() + ": " + YearMonth.now() + "월 정액 비용 5000원 차감");
		return ApiResponse.ok("권한 변경 성공");
	}

	public int getCash(User user) {
		return user.getCash();
	}

	// 요청을 최대 3번까지 할 수 있고 12시간 기준으로 삭제되기에 충전 요청 취소를 만들지 않아도 된다 판단함
	public ApiResponse charging(ChargingRequest chargingRequest, User user) {
		Set<String> keys = redisTemplate.keys("*" + STMT + user.getId() + "-*");

		if (keys.size() >= 3) throw new IllegalArgumentException("처리되지 않은 요청사항이 많습니다");

		String msgValue = chargingRequest.getMsg() + "-" + chargingRequest.getValue();
		Set<String> checkIncorrect = redisTemplate.keys("*" + msgValue + "*");
		if (!checkIncorrect.isEmpty())
			throw new OverlapException("서버내에 중복된 문자가 확인되어 반려되었습니다. 다른 문자로 다시 시대해주세요");

		if (user.getChargingCount() >= 4) throw new BreakTheRulesException();

		String value = user.getId() + "-" + chargingRequest.getMsg() + "-" + chargingRequest.getValue();
		redisTemplate.opsForValue().set(STMT + value, value, 12, TimeUnit.HOURS);
		user.setChargingCount(1);
		userRepository.save(user);
		return ApiResponse.ok("요청 성공");
	}

	public ListApiResponse<ChargingResponse> getCharges(Long userId) {
		Set<String> keys = redisTemplate.keys("*" + STMT + userId + "-*");

		if (keys.size() == 0)
			throw new IllegalArgumentException("충전 요청이 존재하지 않습니다");

		List<String> values = redisTemplate.opsForValue().multiGet(keys);

		List<ChargingResponse> responses = values.stream().map(ChargingResponse::new).collect(Collectors.toList());
		return ListApiResponse.ok("신청 리스트 조회 성공", responses);
	}

	public ListApiResponse<StatementResponse> getStatement(String email) {
		User user = findByUser(email);

		if (user.getStatement().size() == 0)
			throw new IllegalArgumentException("거래내역이 존재하지 않습니다");

		List<StatementResponse> response = user.getStatement()
			.stream()
			.map(str -> {
				String[] localDateMsg = str.split(",");
				return new StatementResponse(localDateMsg);
			})
			.toList();
		return ListApiResponse.ok("거래내역 조회 완료", response);
	}

	private User findByUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
	}
}
