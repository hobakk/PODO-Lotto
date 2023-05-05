package com.example.sixnumber.user.service;

import java.time.LocalDate;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.dto.MapApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
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
	private final LottoRepository lottoRepository;

	public ApiResponse setAdmin(Long userId, HttpServletRequest request) {
		CheckRole(request);
		User user = findByUser(userId);
		user.setAdmin();
		userRepository.save(user);
		return ApiResponse.ok("변경 완료");
	}

	public ListApiResponse<?> getUsers(HttpServletRequest request) {
		CheckRole(request);
		return ListApiResponse.ok("조회 성공", userRepository.findAll());
	}

	public ListApiResponse<?> getChargs(HttpServletRequest request) {
		CheckRole(request);
		return ListApiResponse.ok("조회 성공", cashRepository.findAll());
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

	//초기 로또메인 만들기 위한 코드, 이후 사용할 일이 적어서 코드 중복사용을 안해서 생기는 불이익이 없을거라 생각
	public MapApiResponse<Integer, Integer> createLotto(HttpServletRequest request) {
		CheckRole(request);
		LocalDate today = LocalDate.now();

		HashMap<Integer, Integer> countMap = new HashMap<>();
		for (int i = 1; i <= 45; i++) {
			countMap.put(i, 0);
		}
		Lotto lotto = new Lotto(countMap, today);
		lottoRepository.save(lotto);
		return MapApiResponse.ok("생성 완료", countMap);
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
