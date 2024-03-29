package com.example.sixnumber.global.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class Manager {
	private final JavaMailSender mailSender;

	private final String URL = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=";

	public String getTopNumbersAsString(Map<Integer, Integer> map) {
		return convertIntegerListToString(getTopNumbersAsList(map));
	}

	public List<Integer> getTopNumbersAsList(Map<Integer, Integer> map) {
		return map.entrySet().stream()
				.sorted(Map.Entry.<Integer, Integer> comparingByValue().reversed())
				.limit(6)
				.map(Map.Entry::getKey)
				.sorted()
				.collect(Collectors.toList());
	}

	public String convertIntegerListToString(List<Integer> integerList) {
		StringBuilder sb = new StringBuilder();
		for (int num : integerList) {
			sb.append(num).append(" ");
		}

		return sb.toString().trim();
	}

	public void sendEmail(String email, String authCode) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[포도로또] 이메일 인증번호를 알려드립니다");
		message.setText("인증번호 " + authCode);
		mailSender.send(message);
	}

	public Optional<WinNumberRequest> retrieveLottoResult(int round) {
		String responseBody = getResponseBody(round);
		StringBuilder sb = new StringBuilder();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);

			int count = 1;
			while (true) {
				String index = "drwtNo" + count;
				JsonNode node = jsonNode.get(index);
				if (node == null || node.isNull()) break;

				sb.append(node.asText()).append(" ");
				count++;
			}

			sb.append(jsonNode.get("bnusNo").asText());

			return Optional.ofNullable(WinNumberRequest.builder()
				.date(jsonNode.get("drwNoDate").asText())
				.time(jsonNode.get("drwNo").asInt())
				.prize(jsonNode.get("firstAccumamnt").asLong())
				.winner(jsonNode.get("firstPrzwnerCo").asInt())
				.numbers(sb.toString())
				.build());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Boolean checkMaxRound(int round) {
		String responseBody = getResponseBody(round);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);

			return jsonNode.get("returnValue").asText().equals("true");
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAdmin(User user) {
		return user.getRole() == UserRole.ROLE_ADMIN;
	}

	private String getResponseBody(int round) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(URL + round, String.class);
	}
}