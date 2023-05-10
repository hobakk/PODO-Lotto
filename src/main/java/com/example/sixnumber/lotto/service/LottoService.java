package com.example.sixnumber.lotto.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
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

	public ItemApiResponse<LottoResponse> mainTopNumbers() {
		Lotto lotto = lottoRepository.findById(0L).orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		List<Integer> countList = lotto.getCountList();
		List<Integer> sortedIndices = new ArrayList<>();
		String statistics = "";

		for (int i = 0; i < countList.size(); i++) {
			sortedIndices.add(i);
			statistics = "(" + i+1 + " : " + countList.get(i) + "), ";
			if (i % 9 == 0) {
				statistics = statistics + "\n";
			}
		}

		sortedIndices.sort((index1, index2) -> countList.get(index2).compareTo(countList.get(index1)));
		List<Integer> topIndices = sortedIndices.subList(0, Math.min(sortedIndices.size(), 6));
		Collections.sort(topIndices);
		String result = topIndices.stream().map(Object::toString).collect(Collectors.joining(" "));

		return ItemApiResponse.ok("조회 성공", new LottoResponse(statistics, result));
	}

	public ItemApiResponse<LottoResponse> getTopNumberForMonth(YearMonthRequest request) {
		Lotto lotto = lottoRepository.findByTopNumbersForMonth(request.getYearMonth())
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		LottoResponse response = new LottoResponse(lotto);

		return ItemApiResponse.ok("조회 성공", response);
	}
}
