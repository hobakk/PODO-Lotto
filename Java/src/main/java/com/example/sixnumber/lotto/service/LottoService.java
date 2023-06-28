package com.example.sixnumber.lotto.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class LottoService {

	private final LottoRepository lottoRepository;
	private final Manager manager;

	public ItemApiResponse<LottoResponse> mainTopNumbers() {
		Lotto lotto = lottoRepository.findByMain().orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		List<Integer> countList = lotto.getCountList();
		List<Integer> sortedIndices = new ArrayList<>();
		String statistics = "";

		for (int i = 0; i < countList.size(); i++) {
			sortedIndices.add(i);
			statistics = statistics + "(" + (i+1) + "번 : " + countList.get(i) + "), ";
		}
		statistics = statistics.substring(0, statistics.length() -2);

		String result = manager.reviseResult(sortedIndices, countList);
		return ItemApiResponse.ok("조회 성공", new LottoResponse(statistics, result));
	}

	public ItemApiResponse<LottoResponse> getTopNumberForMonth(YearMonthRequest request) {
		Lotto lotto = lottoRepository.findByTopNumbersForMonth(request.getYearMonth())
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		LottoResponse response = new LottoResponse(lotto);

		return ItemApiResponse.ok("조회 성공", response);
	}
}
