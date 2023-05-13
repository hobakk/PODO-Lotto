package com.example.sixnumber.user.service;

import java.time.YearMonth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ReleasePaidRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.WithdrawRequest;
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
public class UserService {

	private final UserRepository userRepository;
	private final CashRepository cashRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, String> redisTemplate;
	private final String RTK = "RT: ";

	public ApiResponse signUp(SignupRequest request) {
		if (userRepository.existsUserByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이메일 중복");
		}
		if (userRepository.existsUserByNickname(request.getNickname())) {
			throw new IllegalArgumentException("닉네임 중복");
		}

		String password = passwordEncoder.encode(request.getPassword());
		User user = new User(request, password);
		userRepository.save(user);
		return ApiResponse.create("회원가입 완료");
	}

	public String signIn(SigninRequest request) {
		User user = findByUser(request.getEmail());

		if (redisTemplate.opsForValue().get(RTK + user.getId()) != null) {
			redisTemplate.delete(RTK + user.getId());
			throw new IllegalArgumentException("중복 로그인입니다");
		}

		if (user.getStatus().equals(Status.DORMANT)) {
			throw new IllegalArgumentException("탈퇴한 계정입니다");
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

	public ApiResponse withdraw(WithdrawRequest request, String email) {
		String withdrawMsg = "회원탈퇴";

		if (!request.getMsg().equals(withdrawMsg)) {
			throw new IllegalArgumentException("잘못된 문자열 입력");
		}
		User user = findByUser(email);
		user.setStatus("DORMANT");
		redisTemplate.delete(RTK + user.getId());
		return ApiResponse.ok("회원 탈퇴 완료");
	}

	public ApiResponse setPaid(ReleasePaidRequest request, String email) {
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

		return ApiResponse.ok("권한 변경 성공");
	}

	public int getCash(User user) {
		return user.getCash();
	}

	public ApiResponse charging(ChargingRequest chargingRequest, Long userId) {
		Cash cash = new Cash(userId, chargingRequest);
		cashRepository.save(cash);
		return ApiResponse.ok("요청 성공");
	}

	private User findByUser(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}
}
