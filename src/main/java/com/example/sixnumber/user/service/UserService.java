package com.example.sixnumber.user.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
import com.example.sixnumber.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final CashRepository cashRepository;
	private final PasswordEncoder passwordEncoder;

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
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다");
		}
		return JwtProvider.accessToken(user);
	}

	public int getCash(HttpServletRequest request) {
		// user 가 보유한 cash 정보 보안을 위해 Jwt payloads 에 넣지 않아 db에서 user 를 찾음
		User user = findByTokenByUser(request);
		return user.getCash();
	}

	public ApiResponse charging(ChargingRequest chargingRequest, HttpServletRequest httpServletRequest) {
		User user = findByTokenByUser(httpServletRequest);
		Cash cash = new Cash(user, chargingRequest);
		cashRepository.save(cash);
		return ApiResponse.ok("요청 성공");
	}

	private User findByTokenByUser(HttpServletRequest request) {
		Claims claims;
		String token = JwtProvider.resolveToken(request);
		if (token != null) {
			JwtProvider.validateToken(token);
			claims = JwtProvider.getClaims(token);
			String email = claims.getSubject();
			return findByUser(email);
		} else { throw  new IllegalArgumentException("유효하지 않은 토큰"); }
	}

	private User findByUser(String email) {
		return userRepository.findUserByEmail(email)
			.orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}
}
