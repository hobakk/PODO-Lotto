package com.example.sixnumber.lotto.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
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

	private final SixNumberRepository sixNumberRepository;
	private final LottoRepository lottoRepository;
	private final UserRepository userRepository;
	private final Random rd = new Random();

	public ListApiResponse<String> buyNumber(BuyNumberRequest request, User user) {
		confirmationProcess(request, user);

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

		SixNumber sixNumber = new SixNumber(user.getId(), today(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);

		// 임시로 값을 확인하기 위해 ListApiResponse 를 사용
		return ListApiResponse.ok("요청 성공", topNumbers);
	}

	public ListApiResponse<String> buyRepetitionNumber(BuyNumberRequest request, User user) {
		confirmationProcess(request, user);

		List<String> topNumbers = new ArrayList<>();
		int repetition = request.getRepetition();

		for (int i = 0; i < request.getValue(); i++) {
			HashMap<Integer, Integer> countMap = new HashMap<>();

			for (int x = 1; x <= 45; x++) {
				countMap.put(x, 0);
			}

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
			countMap.clear();
		}

		SixNumber sixNumber = new SixNumber(user.getId(), today(), topNumbers);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(topNumbers);
		return ListApiResponse.ok("요청 성공", topNumbers);
	}

	private String today() {
		Date today = new Date();
		SimpleDateFormat yd = new SimpleDateFormat("yyyy-MM");
		return yd.format(today);
	}

	private void confirmationProcess(BuyNumberRequest request, User user) {
		if (request.getRepetition() > 1000) {
			if (user.getCash() < request.getValue() * 200) {
				throw new IllegalArgumentException("포인트가 부족합니다");
			}
			user.setCash("-", request.getValue() * 200);
			userRepository.save(user);
		} else {
			if (user.getCash() < request.getValue() * (request.getRepetition() / 2)) {
				throw new IllegalArgumentException("포인트가 부족합니다");
			}
			user.setCash("-", request.getValue() * 200 * (request.getRepetition() * 10));
			userRepository.save(user);
		}
	}

	// 스캐줄러로 뺄지 말지 고민중 이유 : 한개의 서비스 로직에서 너무 많은 저장이 이루어짐. 영속성 컨텍스트 각이 나오는지도 보고있음
	// 방법 : 저장된 sixNumber 를 시간단위로 모아 한번에 스케줄러로 처리 병렬을 지원하는 java.util.concurrent.ScheduledExecutorService 사용
	private void saveMainLottoList(List<String> list) {
		Lotto lotto = lottoRepository.findById(1L)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정보"));

		List<Integer> countList = lotto.getCountList();
		for (String sentence : list) {
			String[] numbers = sentence.split(" ");

			for (String numberStr : numbers) {
				int num = Integer.parseInt(numberStr) -1;
				countList.set(num, countList.get(num) + 1);
			}
		}
	}
}
