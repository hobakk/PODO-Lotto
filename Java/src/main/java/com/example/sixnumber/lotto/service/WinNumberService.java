package com.example.sixnumber.lotto.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.sixnumber.lotto.dto.WinNumberRequest;
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
	private final int MAX_VIEW = 4;

	@Cacheable(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse getWinNumbers() {
		return adjustWinNumbers();
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse setWinNumbers(int round) {
		Optional<WinNumber> optional = winNumberRepository.findByTime(round);
		WinNumberRequest request = manager.retrieveLottoResult(round)
				.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		if (optional.isPresent()) {
			WinNumber winNumber = optional.get().update(request);
			winNumberRepository.save(winNumber);
		} else {
			try {
				WinNumber winNumber = new WinNumber(request);
				int time = winNumber.getTime();
				int topRound = getFirstWinNumber().getTime();
				if (topRound > 0 && time <= topRound - MAX_VIEW) throw new OverlapException("범위를 벗어났습니다");

				winNumberRepository.save(winNumber);
			} catch (CustomException e) {
				int checkingRound = 1110;
				while (manager.checkMaxRound(checkingRound)) checkingRound++;

				int result = checkingRound - round;
				if (result <= 0 && result >= -MAX_VIEW) {
					WinNumber winNumber = new WinNumber(request);
					winNumberRepository.save(winNumber);
				} else throw new CustomException(ErrorCode.OUT_OF_RANGE);
			}
		}

		return adjustWinNumbers();
	}

	@CachePut(cacheNames = "WinNumbers", key = "'all'")
	public WinNumbersResponse updateCache(WinNumbersResponse response) {
		return response;
	}

	@Cacheable(cacheNames = "WinNumber", key = "'first'")
	public WinNumberResponse getFirstWinNumber() {
		Pageable pageable = PageRequest.of(0, 1);
		List<WinNumber> winNumberList = winNumberRepository.findTopByTime(pageable);
		if (winNumberList.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		return new WinNumberResponse(winNumberList.get(0));
	}

	@CachePut(cacheNames = "WinNumber", key = "'first'")
	public WinNumberResponse updateCacheOfFirstWinNumber(WinNumbersResponse response) {
		return response.retrieveFirstWinNumberAndModifyResponse();
	}

	public WinNumbersResponse adjustWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
		if (winNumberList.size() > MAX_VIEW) {
			List<WinNumber> remainingList = winNumberList.subList(MAX_VIEW, winNumberList.size());
			winNumberRepository.deleteAll(remainingList);
			winNumberList = winNumberList.subList(0, MAX_VIEW);
		}

		return new WinNumbersResponse(winNumberList);
	}
}
