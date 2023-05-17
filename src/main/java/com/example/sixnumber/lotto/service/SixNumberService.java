package com.example.sixnumber.lotto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class SixNumberService {

	private final UserRepository userRepository;
	private final SixNumberRepository sixNumberRepository;
	private final LottoRepository lottoRepository;
	private final Random rd = new Random();

	public ListApiResponse<String> buyNumber(BuyNumberRequest request, User user) {
		confirmationProcess(request, null,  user);

		List<String> topNumbers = new ArrayList<>();
		for (int i = 0; i < request.getValue(); i++) {
			Set<Integer> set = new HashSet<>();

			while (set.size() < 6) {
				int randomNum = rd.nextInt(45) + 1;
				set.add(randomNum);
			}

			List<Integer> numList = new ArrayList<>(set);
			Collections.sort(numList);

			String result = numList.stream().map(Object::toString).collect(Collectors.joining(" "));
			topNumbers.add(result);
		}

		SixNumber sixNumber = new SixNumber(user.getId(), LocalDate.now(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);

		// 임시로 값을 확인하기 위해 ListApiResponse 를 사용
		return ListApiResponse.ok("요청 성공", topNumbers);
	}

	public ListApiResponse<String> statisticalNumber(StatisticalNumberRequest request, User user) {
		confirmationProcess(null, request, user);

		// server 에 올렸을 때 비용문제가 발생할거라 이용에 제한을 줄 필요가 있음
		if (request.getRepetition() != 1000) throw new IllegalArgumentException("규격을 벗어난 반복횟수 입니다");

		List<String> topNumbers = new ArrayList<>();
		HashMap<Integer, Integer> countMap = new HashMap<>();
		for (int x = 1; x <= 45; x++) {
			countMap.put(x, 0);
		}

		int repetition = request.getRepetition();
		for (int i = 0; i < request.getValue(); i++) {

			for (int j = 0; j < repetition; j++) {
				Set<Integer> set = new HashSet<>();

				while (set.size() < 6) {
					int num = rd.nextInt(45) + 1;
					set.add(num);
				}

				for (int num : set) {
					int count = countMap.get(num);
					countMap.put(num, count + 1);
				}
			}

			List<Integer> list = new ArrayList<>(countMap.keySet());
			list.sort((num1, num2) -> countMap.get(num2).compareTo(countMap.get(num1)));

			List<Integer> checkLotto = list.subList(0, 6);
			Collections.sort(checkLotto);
			String result = checkLotto.stream().map(Object::toString).collect(Collectors.joining(" "));
			topNumbers.add(result);
			countMap.replaceAll((key, value) -> 0);
		}

		SixNumber sixNumber = new SixNumber(user.getId(), LocalDate.now(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);
		return ListApiResponse.ok("요청 성공", topNumbers);
	}

	private void confirmationProcess(BuyNumberRequest buyNumberRequest, StatisticalNumberRequest statisticalNumberRequest, User user) {
		int requiredCash = 0;

		if (statisticalNumberRequest == null) {
			requiredCash = buyNumberRequest.getValue() * 200;
		} else if (buyNumberRequest == null) {
			requiredCash = statisticalNumberRequest.getValue() * (statisticalNumberRequest.getRepetition() / 2);
		} else {
			throw new IllegalArgumentException("정보가 옳바르지 않습니다");
		}

		if (user.getCash() < requiredCash) {
			throw new IllegalArgumentException("금액이 부족합니다");
		}

		user.setCash("-", requiredCash);
		userRepository.save(user);
	}

	// 스캐줄러로 뺄지 말지 고민중 이유 : 한개의 서비스 로직에서 너무 많은 저장이 이루어짐. 영속성 컨텍스트 각이 나오는지도 보고있음
	// 방법 : 저장된 sixNumber 를 시간단위로 모아 한번에 스케줄러로 처리 병렬을 지원하는 java.util.concurrent.ScheduledExecutorService 사용
	private void saveMainLottoList(List<String> list) {
		Lotto lotto = lottoRepository.findByMain()
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정보"));

		List<Integer> countList = lotto.getCountList();
		for (String sentence : list) {
			String[] numbers = sentence.split(" ");

			for (String numberStr : numbers) {
				int num = Integer.parseInt(numberStr) - 1;
				countList.set(num, countList.get(num) + 1);
			}
		}
	}
}
