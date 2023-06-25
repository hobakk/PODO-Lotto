package com.example.sixnumber.test.service;

import org.springframework.stereotype.Service;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.test.entity.TestUser;
import com.example.sixnumber.test.repository.TestUserRepository;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TestUserService {

	private final UserRepository userRepository;
	private final TestUserRepository testUserRepository;

	public ApiResponse deleteUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정보"));
		userRepository.delete(user);
		return ApiResponse.ok("삭제완료");
	}
	public ApiResponse patchUser(Long userId, OnlyMsgRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정보"));
		user.setNickname(request.getMsg());
		return ApiResponse.ok("수정완료");
	}
	public ApiResponse createUser(SigninRequest request) {
		TestUser tUser = new TestUser(request.getEmail(), request.getPassword());
		System.out.println(request.getEmail() + ", " + request.getPassword());
		testUserRepository.save(tUser);
		return ApiResponse.ok("생성완료");
	}
}
