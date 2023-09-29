package com.example.sixnumber.lotto.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.example.sixnumber.user.entity.Statement;
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

	private static final String RANDOM_NUMBER = "buyNumber";
	private static final String PREMIUM_NUMBER = "statisticalNumber";

	public UnifiedResponse<List<String>> buyNumber(BuyNumberRequest request, User user) {
		handlerEventAndPayment(RANDOM_NUMBER, request.getValue(), 0,  user.getId());

		List<String> topNumbers = new ArrayList<>();
		for (int i = 0; i < request.getValue(); i++) {
			Set<Integer> set = new HashSet<>();

			while (set.size() < 6) {
				int randomNum = rd.nextInt(45) + 1;
				set.add(randomNum);
			}

			String result = new ArrayList<>(set).stream()
				.sorted()
				.map(Objects::toString)
				.collect(Collectors.joining(" "));

			topNumbers.add(result);
		}

		sixNumberRepository.save(new SixNumber(user, LocalDateTime.now(), topNumbers));
		saveMainLottoList(topNumbers);
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<List<String>> statisticalNumber(StatisticalNumberRequest request, User user) {
		handlerEventAndPayment(PREMIUM_NUMBER, request.getValue(), request.getRepetition(), user.getId());

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

		sixNumberRepository.save(new SixNumber(user, LocalDateTime.now(), topNumbers));
		saveMainLottoList(topNumbers);
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<List<String>> getRecentBuyNumbers(User user) {
		Pageable pageable = PageRequest.of(0, 1);
		List<SixNumber> recentBuyNumberList = sixNumberRepository.findByRecentBuyNumbers(user, pageable);
		if (recentBuyNumberList.size() == 0) throw new CustomException(NO_MATCHING_INFO_FOUND);

		return UnifiedResponse.ok("최근 구매 번호 조회 성공", recentBuyNumberList.get(0).getNumberList());
	}

	private void handlerEventAndPayment(String event, int generationCount, int repeatCount, Long userId) {
		User user = manager.findUser(userId);

		int payment;
		Statement statement;
		if (event.equals(RANDOM_NUMBER)) {
			payment = generationCount * 100;
			statement = new  Statement(user, "랜덤 번호 " + generationCount + "회 발급", payment);
		} else {
			payment = generationCount * 200;
			statement = new Statement(user, "프리미엄 번호 " + generationCount + "회 발급",
				payment, repeatCount + " 회 반복 처리");
		}

		if (user.getCash() < payment) throw new IllegalArgumentException("금액이 부족합니다");

		user.minusCash(payment);
		user.addStatement(statement);
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
