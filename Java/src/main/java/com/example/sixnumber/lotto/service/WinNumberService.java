package com.example.sixnumber.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Cacheable(value = "WinNumbers", key = "'all'")
	public WinNumberResponse getWinNumbers() {
		return transform(getLatestWinNumbers());
	}

	@CachePut(value = "WinNumbers", key = "'all'")
	public WinNumberResponse setWinNumbers(int round) {
		WinNumber winNumber = manager.retrieveLottoResult(round)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		int time = winNumber.getTime();
		if (winNumberRepository.existsWinNumberByTime(time))
			throw new OverlapException("이미 등록된 당첨 결과 입니다");

		winNumberRepository.save(winNumber);
		return transform(getLatestWinNumbers());
	}

	private List<WinNumber> getLatestWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
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
