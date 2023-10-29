package com.example.sixnumber.lotto.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class WinNumberService {
	private final WinNumberRepository winNumberRepository;

	@Cacheable(value = "WinNumbers", key = "'all'")
	public WinNumberResponse getWinNumbers() {
		List<WinNumber> winNumberList = findAllAfterCheckIsEmpty();
		return transform(winNumberList);
	}

	@CachePut(value = "WinNumbers", key = "'all'")
	public WinNumberResponse setWinNumbers(WinNumberRequest request) {
		WinNumber winNumber = new WinNumber(request);
		int time = winNumber.getTime();
		List<Integer> topNumberList = winNumber.getTopNumberList();

		if (winNumberRepository.existsWinNumberByTimeAndTopNumberListIn(time, topNumberList))
			throw new OverlapException("이미 등록된 당첨 결과 입니다");

		winNumberRepository.save(winNumber);

		List<WinNumber> winNumberList = findAllAfterCheckIsEmpty();

		return transform(winNumberList.stream()
			.sorted(Comparator.comparing(WinNumber::getTime).reversed())
			.skip(Math.max(0, winNumberList.size() - 5))
			.collect(Collectors.toList()));
	}

	private List<WinNumber> findAllAfterCheckIsEmpty() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

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
