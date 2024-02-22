package com.example.sixnumber.lotto.service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class LottoService {

	private final LottoRepository lottoRepository;
	private final SixNumberRepository sixNumberRepository;
	private final Manager manager;

	@Cacheable(cacheNames = "MainStats", key = "'all'")
	public LottoResponse mainTopNumbers() {
		return lottoRepository.findByMain()
			.map(lotto -> {
				Map<Integer, Integer> map = new HashMap<>();
				for (int i = 0; i < lotto.getCountList().size(); i++) {
					map.put(i + 1, lotto.getCountList().get(i));
				}

				String result = manager.getTopNumbersAsString(map);
				return new LottoResponse(lotto.getCountList(), result);
			})
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(value = "MonthlyStats", key = "#yearMonth")
	public LottoResponse getTopNumberForMonth(YearMonth yearMonth) {
		return lottoRepository.findByTopNumbersForMonth(yearMonth)
			.map(lotto -> new LottoResponse(lotto.getCountList(), lotto.getTopNumber()))
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(cacheNames = "MonthlyStatsIndex", key = "'all'")
	public YearMonthResponse getAllMonthStats() {
		return new YearMonthResponse(getAllMonthIndex());
	}

	@CachePut(cacheNames = "MonthlyStatsIndex", key = "'all'")
	public YearMonthResponse updateCacheWithAllMonthlyStatsIndex() {
		return new YearMonthResponse(getAllMonthIndex());
	}

	public UnifiedResponse<?> createMonthlyReport(int year, int month) {
		YearMonth yearMonth = YearMonth.of(year, month);
		if (lottoRepository.existsLottoByCreationDate(yearMonth)
			|| yearMonth.equals(YearMonth.now())
			|| yearMonth.isAfter(YearMonth.now())
		) throw new IllegalArgumentException("이미 처리되었거나 잘못된 입력값 입니다");

		Map<Integer, Integer> map = new HashMap<>();
		List<SixNumber> sixNumberList = sixNumberRepository.findAllByBuyDate(year, month);
		if (sixNumberList.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		sixNumberList.forEach(sixNumber -> sixNumber.getNumberList()
			.forEach(sentence -> Stream.of(sentence.split(" "))
				.forEach(topNumberStr -> {
					int key = Integer.parseInt(topNumberStr);
					map.put(key, map.getOrDefault(key, 0) + 1);
				})
			)
		);

		String result = manager.getTopNumbersAsString(map);

		List<Integer> countList = IntStream.rangeClosed(1, 45)
			.mapToObj(i -> map.getOrDefault(i, 0))
			.collect(Collectors.toList());

		Lotto lotto = new Lotto("Stats", "Scheduler", yearMonth, countList, result);
		lottoRepository.save(lotto);
		updateCacheWithAllMonthlyStatsIndex();
		return UnifiedResponse.ok("월별 통계 생성완료");
	}

	private List<String> getAllMonthIndex() {
		List<String> yearMonthList = lottoRepository.findAllBySubject("Stats").stream()
			.map(lotto -> lotto.getCreationDate().toString())
			.collect(Collectors.toList());

		if (yearMonthList.isEmpty()) throw new IllegalArgumentException("해당 정보를 찾을 수 없습니다");

		return yearMonthList;
	}
}
