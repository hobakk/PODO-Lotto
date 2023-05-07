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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.dto.MapApiResponse;
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
	private final int num = rd.nextInt(45) + 1;

	public ListApiResponse<Integer> buyNumber(BuyNumberRequest request, User user) {
		confirmationProcess(request, user);

		List<Integer> numberList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			if (!numberList.contains(num)) {
				numberList.add(num);
			}
			if (numberList.size() == 6) {
				break;
			}
		}
		Collections.sort(numberList);
		SixNumber sixNumber = new SixNumber(user.getId(), today(), numberList);
		sixNumberRepository.save(sixNumber);
		saveMainLottoList(numberList);

		// 임시로 값을 확인하기 위해 ListApiResponse 를 사용
		return ListApiResponse.ok("요청 성공", numberList);
	}

	public ListApiResponse<Integer> buyRepetitionNumber(BuyNumberRequest request, User user) {
		confirmationProcess(request, user);

		List<HashSet<Integer>> setList = new ArrayList<>();
		List<Integer> numberList = new ArrayList<>(6);
		int repetition = request.getRepetition();

		for (int i = 0; i < request.getValue(); i++) {
			HashMap<Integer, Integer> countMap = new HashMap<>();
			for (int x = 1; x <= 45; x++) {
				countMap.put(x, 0);
			}

			for (int j = 0; j < repetition; j++) {
				HashSet<Integer> lottoList = new HashSet<>(6);
				for (int x = 0; x < 15; x++) {
					lottoList.add(num);
					if (lottoList.size() == 6) {
						setList.add(lottoList);
						break;
					}
				}
			}

			for (HashSet<Integer> lotto : setList) {
				for (Integer num : lotto) {
					int count = countMap.get(num);
					countMap.put(num, count + 1);
				}
			}
			System.out.println(countMap);

			List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(countMap.entrySet());
			entryList.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());

			for (int x = 0; x < 6; x++) {
				int checknum = entryList.get(x).getKey();
				numberList.add(checknum);
				setList.clear();
			}
			Collections.sort(numberList);
			SixNumber sixNumber = new SixNumber(user.getId(), today(), numberList);
			sixNumberRepository.save(sixNumber);
			saveMainLottoList(numberList);
		}
		System.out.println(numberList);
		return ListApiResponse.ok("요청 성공", numberList);
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
	private void saveMainLottoList(List<Integer> list) {
		Lotto lotto = lottoRepository.findById(0L)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 정보"));
		List<Integer> countList = lotto.getCountList();
		for (int num : list) {
			countList.set(num, +1);
		}
		lottoRepository.save(lotto);
	}
}
