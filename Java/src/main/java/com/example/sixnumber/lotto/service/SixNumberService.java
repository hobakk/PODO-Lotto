package com.example.sixnumber.lotto.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class SixNumberService {

	private final SixNumberRepository sixNumberRepository;
	private final LottoRepository lottoRepository;
	private final Manager manager;
	private final Random rd = new Random();

	public UnifiedResponse<List<String>> buyNumber(BuyNumberRequest request, User user) {
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

		SixNumber sixNumber = new SixNumber(user.getId(), LocalDateTime.now(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);

		// 임시로 값을 확인하기 위해 ListApiResponse 를 사용
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<List<String>> statisticalNumber(StatisticalNumberRequest request, User user) {
		confirmationProcess(null, request, user);

		// server 에 올렸을 때 비용문제가 발생할거라 이용에 제한을 줄 필요가 있음
		// if (request.getRepetition() != 1000) throw new IllegalArgumentException("규격을 벗어난 반복횟수 입니다");

		List<String> topNumbers = new ArrayList<>();
		HashMap<Integer, Integer> countMap = new HashMap<>();
		for (int x = 1; x <= 45; x++) {
			countMap.put(x, 0);
		}

		int value = request.getValue();
		int repetition = request.getRepetition();
		ExecutorService executorService = Executors.newFixedThreadPool(value);

		for (int i = 0; i < value; i++) {
			executorService.execute(() -> {
				Map<Integer, Integer> localCountMap = new HashMap<>(countMap);
				for (int j = 0; j < repetition; j++) {
					Set<Integer> set = new HashSet<>();

					while (set.size() < 6) {
						int num = rd.nextInt(45) + 1;
						set.add(num);
					}

					for (int num : set) {
						int count = localCountMap.get(num);
						localCountMap.put(num, count + 1);
					}
				}

				List<Integer> list = new ArrayList<>(localCountMap.keySet());
				list.sort((num1, num2) -> localCountMap.get(num2).compareTo(localCountMap.get(num1)));

				List<Integer> checkLotto = list.subList(0, 6);
				Collections.sort(checkLotto);
				String result = checkLotto.stream().map(Object::toString).collect(Collectors.joining(" "));
				synchronized (topNumbers) {
					topNumbers.add(result);
				}
			});
		}
		executorService.shutdown();

		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		SixNumber sixNumber = new SixNumber(user.getId(), LocalDateTime.now(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<SixNumber> getRecentBuyNumbers(User user) {
		SixNumber recentBuyNumbers = sixNumberRepository.findByRecentBuyNumbers(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("해당 정보가 존재하지 않습니다"));
		return UnifiedResponse.ok("최근 구매 번호 조회 성공", recentBuyNumbers);
	}

	private void confirmationProcess(BuyNumberRequest buyNumberRequest, StatisticalNumberRequest statisticalNumberRequest, User userIf) {
		User user = manager.findUser(userIf.getId());
		int requiredCash = 0;
		String msg = "";

		if (statisticalNumberRequest == null) {
			requiredCash = buyNumberRequest.getValue() * 200;
			msg = "추첨번호 " + buyNumberRequest.getValue() + "회 구매 : " + requiredCash + "원 차감";
		} else if (buyNumberRequest == null) {
			requiredCash = statisticalNumberRequest.getValue() * 300;
			msg = statisticalNumberRequest.getRepetition() + "번 반복 TOP 6 " + statisticalNumberRequest.getValue() + "회 구매 : " + requiredCash + "원 차감";
		} else throw new CustomException(INVALID_INPUT);

		if (user.getCash() < requiredCash) throw new IllegalArgumentException("금액이 부족합니다");

		user.setCash("-", requiredCash);
		user.setStatement(LocalDate.now() + "," + msg);
	}

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
