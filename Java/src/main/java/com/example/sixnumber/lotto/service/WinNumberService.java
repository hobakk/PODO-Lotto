package com.example.sixnumber.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.sixnumber.lotto.dto.WinNumberResponse;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.dto.WinNumbersResponse;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class WinNumberService {
	private final WinNumberRepository winNumberRepository;
	private final Manager manager;

	@Cacheable(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse getWinNumbers() {
		return transform(getSortingWinNumbers());
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse setWinNumbers(int round) {
		WinNumber winNumber = manager.retrieveLottoResult(round)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		try {
			int time = winNumber.getTime();
			int topRound = getFirstWinNumber().getTime();
			if (topRound > 0 && time <= topRound - 5 || winNumberRepository.existsWinNumberByTime(time))
				throw new OverlapException("등록된 당첨 결과 이거나 범위를 벗어났습니다");

			winNumberRepository.save(winNumber);
		} catch (CustomException e) {
			int checkingRound = 1110;
			while (manager.checkMaxRound(checkingRound)) checkingRound++;

			int result = checkingRound - round;
			if (result <= 0 && result >= -5) winNumberRepository.save(winNumber);
			else throw new CustomException(ErrorCode.OUT_OF_RANGE);
		}

		return transform(getSortingWinNumbers());
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse updateCache(List<WinNumber> winNumberList) {
		return transform(winNumberList);
	}

	@Cacheable(cacheNames = "WinNumber", key = "'first'")
	public WinNumberResponse getFirstWinNumber() {
		Pageable pageable = PageRequest.of(0, 1);
		List<WinNumber> winNumberList = winNumberRepository.findTopByTime(pageable);
		if (winNumberList.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		WinNumber winNumber = winNumberList.get(0);
		return new WinNumberResponse(
				winNumber.getTime(),
				winNumber.getDate(),
				winNumber.getTopNumberList(),
				winNumber.getBonus()
		);
	}

	@CachePut(cacheNames = "WinNumber", key = "'first'")
	public WinNumber updateCacheOfFirstWinNumber(WinNumber firstWinNumber) {
		return firstWinNumber;
	}

	private List<WinNumber> getSortingWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
		return winNumberList;
	}

	private WinNumbersResponse transform(List<WinNumber> winNumberList) {
		List<TransformResponse> transformList = winNumberList.stream()
			.map(winNumber -> new TransformResponse(
				winNumber.getDate(),
				winNumber.getTime(),
				winNumber.getPrize(),
				winNumber.getWinner(),
				winNumber.getTopNumberList(),
				winNumber.getBonus())
			)
			.collect(Collectors.toList());

		return new WinNumbersResponse(transformList);
	}
}
