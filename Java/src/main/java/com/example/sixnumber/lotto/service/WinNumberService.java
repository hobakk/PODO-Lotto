package com.example.sixnumber.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class WinNumberService {
	private final WinNumberRepository winNumberRepository;
	private final String URL = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=";

	@Cacheable(value = "WinNumbers", key = "'all'")
	public WinNumberResponse getWinNumbers() {
		return transform(getWinNumberList());
	}

	@CachePut(value = "WinNumbers", key = "'all'")
	public WinNumberResponse setWinNumbers(WinNumberRequest request) {
		WinNumber winNumber = new WinNumber(request);
		int time = winNumber.getTime();
		List<Integer> topNumberList = winNumber.getTopNumberList();

		if (winNumberRepository.existsWinNumberByTimeAndTopNumberListIn(time, topNumberList))
			throw new OverlapException("이미 등록된 당첨 결과 입니다");

		winNumberRepository.save(winNumber);

		return transform(getWinNumberList());
	}

	private Optional<WinNumberRequest> retrieveLottoResult(String round) {
		RestTemplate restTemplate = new RestTemplate();
		String responseBody = restTemplate.getForObject(URL + round, String.class);
		StringBuilder sb = new StringBuilder();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);

			int count = 1;
			while (true) {
				String index = "drwNo" + count;
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

	private List<WinNumber> findAllAfterCheckIsEmpty() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		return winNumberList;
	}

	private List<WinNumber> getWinNumberList() {
		List<WinNumber> winNumberList = findAllAfterCheckIsEmpty().stream()
			.sorted(Comparator.comparing(WinNumber::getTime).reversed())
			.collect(Collectors.toList());

		if (winNumberList.size() > 5) winNumberList = winNumberList.subList(0, 5);

		return winNumberList;
	}

	private WinNumberResponse transform(List<WinNumber> winNumberList) {
		List<TransformResponse> transformList = winNumberList.stream()
			.map(winNumber -> new TransformResponse(
				winNumber.getData(),
				winNumber.getTime(),
				winNumber.getPrize(),
				winNumber.getWinner(),
				winNumber.getTopNumberList(),
				winNumber.getBonus())
			)
			.collect(Collectors.toList());

		return new WinNumberResponse(transformList);
	}
}
