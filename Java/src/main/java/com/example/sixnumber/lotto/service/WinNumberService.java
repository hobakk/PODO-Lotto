package com.example.sixnumber.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberResponse;

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
		if (winNumberRepository.findByTimeAndTopNumberList(winNumber.getTime(), winNumber.getTopNumberList())) {
			throw new IllegalArgumentException("이미 등록된 정보입니다");
		}
		winNumberRepository.save(winNumber);

		List<WinNumber> winNumberList = findAllAfterCheckIsEmpty();
		if (winNumberList.size() >= 5) {
			winNumberList = winNumberList.subList(winNumberList.size()-5, winNumberList.size());
		}

		return transform(winNumberList);
	}

	private List<WinNumber> findAllAfterCheckIsEmpty() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		return winNumberList;
	}

	private WinNumberResponse transform(List<WinNumber> winNumberList) {
		List<TransformResponse> responses = new ArrayList<>();
		for (WinNumber winNumber : winNumberList) {
			responses.add(new TransformResponse(winNumber.getData(), winNumber.getTime(), winNumber.getPrize(),
				winNumber.getWinner(), winNumber.getTopNumberList(), winNumber.getBonus()));
		}

		return new WinNumberResponse(responses);
	}
}
