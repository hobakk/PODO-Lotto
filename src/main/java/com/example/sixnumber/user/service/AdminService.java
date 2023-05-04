package com.example.sixnumber.user.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.DataApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.UserRole;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AdminService {

	private final CashRepository cashRepository;
	private final UserRepository userRepository;

	public ApiResponse setAdmin(Long userId, HttpServletRequest request) {
		CheckRole(request);
		User user = findByUser(userId);
		user.setAdmin();
		userRepository.save(user);
		return ApiResponse.ok("변경 완료");
	}

	public DataApiResponse<?> getUsers(HttpServletRequest request) {
		CheckRole(request);
		return DataApiResponse.ok("조회 성공", userRepository.findAll());
	}

	public DataApiResponse<?> getChargs(HttpServletRequest request) {
		CheckRole(request);
		return DataApiResponse.ok("조회 성공", cashRepository.findAll());
	}

	public ApiResponse upCash(CashRequest cashRequest, HttpServletRequest httpServletRequest) {
		CheckRole(httpServletRequest);
		User user = findByUser(cashRequest.getUserId());
		user.setCash("+", cashRequest.getValue());
		userRepository.save(user);
		return ApiResponse.ok("충전 완료");
	}

	public ApiResponse downCash(CashRequest cashRequest, HttpServletRequest httpServletRequest) {
		CheckRole(httpServletRequest);
		User user = findByUser(cashRequest.getUserId());
		user.setCash("-", cashRequest.getValue());
		userRepository.save(user);
		return ApiResponse.ok("차감 완료");
	}

	private void CheckRole(HttpServletRequest request) {
		Claims claims;
		String token = JwtProvider.resolveToken(request);
		if (token != null) {
			JwtProvider.validateToken(token);
			claims = JwtProvider.getClaims(token);
			UserRole role = (UserRole)claims.get("role");
			if (!role.equals(UserRole.ROLE_ADMIN)) {
				throw  new IllegalArgumentException("권한이 없습니다");
			}
		} else { throw  new IllegalArgumentException("유효하지 않은 토큰"); }
	}

	private User findByUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}
}
