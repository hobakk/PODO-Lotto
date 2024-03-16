package com.example.sixnumber.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.sixnumber.lotto.dto.WinNumberResponse;
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
	public WinNumberResponse getWinNumbers() {
		return transform(getSortingWinNumbers());
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumberResponse setWinNumbers(int round) {
		WinNumber winNumber = manager.retrieveLottoResult(round)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		int time = winNumber.getTime();
		int topRound = getFirstWinNumber().getTime();
		if (topRound > 0 && time <= topRound - 5 || winNumberRepository.existsWinNumberByTime(time))
			throw new OverlapException("등록된 당첨 결과 이거나 범위를 벗어났습니다");

		winNumberRepository.save(winNumber);
		return transform(getSortingWinNumbers());
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumberResponse updateCache(List<WinNumber> winNumberList) {
		return transform(winNumberList);
	}

	@Cacheable(cacheNames = "WinNumber", key = "'first'")
	public WinNumber getFirstWinNumber() {
		Pageable pageable = PageRequest.of(0, 1);
		List<WinNumber> winNumberList = winNumberRepository.findTopByTime(pageable);
		if (winNumberList.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		return winNumberList.get(0);
	}

	private List<WinNumber> getSortingWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
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
