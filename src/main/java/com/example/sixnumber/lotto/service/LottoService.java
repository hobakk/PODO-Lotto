package com.example.sixnumber.lotto.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.lotto.dto.SchedulerResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class LottoService {

	private final LottoRepository lottoRepository;

	public void FindByTopNumberListForMonth(SchedulerResponse<Integer> response) {

		// List<Integer> countList = new ArrayList<>(45);
		// for (int i = 0; i < 45; i++) {
		// 	countList.add(1);
		// }
		//
		// for (int i = 0; i < response.getNumList().size(); i++) {
		// 	List<Integer> numberList = response.getNumList().get(i);
		// 	for (int num : numberList) {
		// 		countList.set(num, countList.get(num) + 1);
		// 	}
		// }
		//
		// Lotto lotto = new Lotto("main", "Scheduler", YearMonth.now(), countList);
		// lottoRepository.save(lotto);
	}
}
