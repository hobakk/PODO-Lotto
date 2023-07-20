package com.example.sixnumber.global.util;

import static com.example.sixnumber.global.exception.ErrorCode.*;

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

	public String reviseResult(List<Integer> sortedIndices, List<Integer> countList) {
		sortedIndices.sort((index1, index2) -> countList.get(index2).compareTo(countList.get(index1)));
		List<Integer> topIndices = sortedIndices.subList(0, Math.min(sortedIndices.size(), 6));
		Collections.sort(topIndices);
		topIndices.replaceAll(Integer -> Integer + 1);
		return topIndices.stream().map(Object::toString).collect(Collectors.joining(" "));
	}
}
