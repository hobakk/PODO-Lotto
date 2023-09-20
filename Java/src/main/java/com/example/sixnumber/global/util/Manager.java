package com.example.sixnumber.global.util;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class Manager {

	private final UserRepository userRepository;
	private final JavaMailSender mailSender;

	public User findUser(Object object) {
		if (object instanceof Long) {
			return userRepository.findById((long) object).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		} else if (object instanceof String) {
			return userRepository.findByEmail((String) object).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
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

	public void sendEmail(String email, String authCode) {
		boolean isNotNull = Stream.of(email, authCode).allMatch(Objects::nonNull);
		if (!isNotNull) throw new CustomException(INVALID_INPUT);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[포도로또] 이메일 인증번호를 알려드립니다");
		message.setText("인증번호 " + authCode);
		mailSender.send(message);
	}
}