package com.example.sixnumber.global.util;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class Manager {

	private final UserRepository userRepository;

	public User findUser(Object object) {
		if (object instanceof Long userId) {
			return userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		} else if (object instanceof String email) {
			return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		} else throw new IllegalArgumentException("잘못된 접근입니다");
	}

	public String revisedTopIndicesAsStr(List<Integer> countList) {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < countList.size(); i++) { indices.add(i); }

		indices.sort((i1, i2) -> Integer.compare(countList.get(i2), countList.get(i1)));

		List<Integer> integers = indices.subList(0, Math.min(6, countList.size()));
		integers.replaceAll(Integer -> Integer + 1);
		Collections.sort(integers);
		return integers.stream()
			.map(Object::toString)
			.collect(Collectors.joining(" "));
	}
}
