package com.example.sixnumber.lotto.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.Statement;
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
	private final Manager manager;
	private final Random rd = new Random();

	public UnifiedResponse<List<String>> buyNumber(int total) {
		List<String> topNumbers = IntStream.range(0, total)
				.mapToObj(n -> getRandomNumberSet().stream()
						.sorted()
						.map(Objects::toString)
						.collect(Collectors.joining(" "))
						.trim()
				)
				.collect(Collectors.toList());
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<List<String>> statisticalNumber(StatisticalNumberRequest request, User user) {
		List<String> topNumbers = IntStream.range(0, request.getValue())
				.parallel()
				.mapToObj(i -> IntStream.range(0, request.getRepetition())
						.mapToObj(j -> {
							Map<Integer, Integer> map = new HashMap<>();
							Set<Integer> set = getRandomNumberSet();
							set.forEach(num -> {
								map.put(num, map.getOrDefault(num, 0) + 1);
							});

							return manager.getTopNumbersAsString(map);
						})
						.collect(Collectors.toList())
				)
				.flatMap(List::stream)
				.collect(Collectors.toList());

		sixNumberRepository.save(new SixNumber(user, LocalDateTime.now(), topNumbers));
		saveMainLottoList(topNumbers);
		return UnifiedResponse.ok("요청 성공", topNumbers);
	}

	public UnifiedResponse<List<String>> getRecentBuyNumbers(User user) {
		Pageable pageable = PageRequest.of(0, 1);
		List<SixNumber> recentBuyNumberList = sixNumberRepository.findByRecentBuyNumbers(user, pageable);
		if (recentBuyNumberList.isEmpty()) throw new CustomException(NO_MATCHING_INFO_FOUND);

		return UnifiedResponse.ok("최근 구매 번호 조회 성공", recentBuyNumberList.get(0).getNumberList());
	}

	private void saveMainLottoList(List<String> topNumbersList) {
		lottoRepository.findByMain()
			.ifPresent(lotto -> {
				List<Integer> countList = lotto.getCountList();

				topNumbersList.forEach(sentence -> {
					String[] numbers = sentence.split(" ");
					Stream.of(numbers).forEach(numberStr -> {
						int num = Integer.parseInt(numberStr) - 1;
						countList.set(num, countList.get(num) + 1);
					});
				});
			});
	}

	private Set<Integer> getRandomNumberSet() {
		Set<Integer> set = new HashSet<>();
		while (set.size() < 6) {
			int num = rd.nextInt(45) + 1;
			set.add(num);
		}

		return set;
	}
}
