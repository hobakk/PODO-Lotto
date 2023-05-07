package com.example.sixnumber.user.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.StatusRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
import com.example.sixnumber.user.repository.UserRepository;
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

	public ApiResponse setAdmin(User user, Long userId) {
		confirmationProcess(user, userId);
		User target = findByUser(userId);
		target.setAdmin();
		userRepository.save(target);
		return ApiResponse.ok("변경 완료");
	}

	// page 처리 필요함
	public ListApiResponse<?> getUsers() {
		return ListApiResponse.ok("조회 성공", userRepository.findAll());
	}

	public ListApiResponse<?> getChargs() {
		return ListApiResponse.ok("조회 성공", cashRepository.findAll());
	}

	public ApiResponse upCash(CashRequest cashRequest) {
		User user = findByUser(cashRequest.getUserId());
		user.setCash("+", cashRequest.getValue());
		userRepository.save(user);
		return ApiResponse.ok("충전 완료");
	}

	public ApiResponse downCash(CashRequest cashRequest) {
		User user = findByUser(cashRequest.getUserId());
		user.setCash("-", cashRequest.getValue());
		userRepository.save(user);
		return ApiResponse.ok("차감 완료");
	}

	//초기 로또메인 만들기 위한 코드, 이후 사용할 일이 적어서 코드 중복사용을 안해서 생기는 불이익이 없을거라 생각
	public ApiResponse createLotto(String email) {
		Date date = new Date();
		SimpleDateFormat yd = new SimpleDateFormat("yyyy-MM");
		String today = yd.format(date);

		List<Integer> countList = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			countList.add(1);
		}
		Lotto lotto = new Lotto(today, email, countList);
		lottoRepository.save(lotto);
		return ApiResponse.ok("생성 완료");
	}

	public ApiResponse setStatus(User user, Long userId, StatusRequest request) {
		confirmationProcess(user, userId);
		User target = findByUser(userId);
		target.setStatus(request.getMsg());
		userRepository.save(target);
		return ApiResponse.ok("상태 변경 완료");
	}

	private User findByUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}

	private void confirmationProcess(User user, Long userId) {
		if (userId != null) {
			if (user.getId().equals(userId)) {
				throw new IllegalArgumentException("본인 입니다");
			}
		}
		if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
			throw new IllegalArgumentException("운영자 계정입니다");
		}
	}
}
