package com.example.sixnumber.global.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class Manager {
	private final JavaMailSender mailSender;

	private final String URL = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=";

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
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[포도로또] 이메일 인증번호를 알려드립니다");
		message.setText("인증번호 " + authCode);
		mailSender.send(message);
	}

	public Optional<WinNumberRequest> retrieveLottoResult(int round) {
		RestTemplate restTemplate = new RestTemplate();
		String responseBody = restTemplate.getForObject(URL + round, String.class);
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
}