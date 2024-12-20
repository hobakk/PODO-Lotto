package com.example.sixnumber.lotto.service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.example.sixnumber.global.exception.OverlapException;
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

	public UnifiedResponse<?> createLotto() {
		return lottoRepository.findByMain()
				.map(main -> {
					main.Initialization();
					return UnifiedResponse.ok("초기화 완료");
				})
				.orElseGet(() -> {
					List<Integer> countList = new ArrayList<>(Collections.nCopies(45, 1));
					Lotto lotto = new Lotto("main", "ADMIN", countList);
					lottoRepository.save(lotto);
					return UnifiedResponse.ok("생성 완료");
				});
	}

	public Boolean checkMain() {
		return lottoRepository.existsLottoBySubject("main");
	}

	@Cacheable(value = "Report", key = "'main'")
	public LottoResponse mainTopNumbers() {
		return lottoRepository.findByMain()
			.map(lotto -> {
				Map<Integer, Integer> map = new HashMap<>(45);
				for (int i = 0; i < lotto.getCountList().size(); i++) {
					map.put(i + 1, lotto.getCountList().get(i));
				}

				String result = manager.getTopNumbersAsString(map);
				return new LottoResponse(lotto.getCountList(), result);
			})
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(cacheNames = "Stats", key = "#yearMonth")
	public LottoResponse getMonthlyStats(YearMonth yearMonth) {
		return lottoRepository.findByTopNumbersForMonth(yearMonth)
			.map(LottoResponse::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(cacheNames = "StatsIndex", key = "'all'")
	public YearMonthResponse getAllMonthlyStats() {
		return getAllStatsIndexBySubject("Stats");
	}

	public UnifiedResponse<?> createMonthlyReport(int year, int month) {
		YearMonth yearMonth = YearMonth.of(year, month);
		if (lottoRepository.existsLottoByCreationDate(yearMonth)
			|| yearMonth.equals(YearMonth.now())
			|| yearMonth.isAfter(YearMonth.now())
		) throw new IllegalArgumentException("이미 처리되었거나 잘못된 입력값 입니다");

		Map<Integer, Integer> map = new HashMap<>(45);
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

		saveLottoResult("Stats", map, yearMonth);
		return UnifiedResponse.ok("월별 통계 생성완료");
	}

	public UnifiedResponse<?> createYearlyReport(int year) {
		String index = year + "Stats";
		int currentYear = YearMonth.now().getYear();
		if (lottoRepository.existsLottoBySubject(index) || year == currentYear)
			throw new OverlapException(year + "년도 통계가 이미 생성됬거나 생성할 수 없는 상태입니다");

		List<List<Integer>> countListOfMonthlyReport = lottoRepository.findAllBySubject(index).stream()
				.findAny()
				.map(Lotto::getCountList)
				.map(Collections::singletonList)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		Map<Integer, Integer> map = new HashMap<>(45);
		for (List<Integer> list : countListOfMonthlyReport) {
			int count = 0;
			while (count < list.size()) {
				map.put(count +1, map.getOrDefault(count +1, 0) + list.get(count));
				count++;
			}
		}

		saveLottoResult("yearlyStats", map, YearMonth.now());
		return UnifiedResponse.ok( year + "년 통계 생성 성공");
	}

	@Cacheable(cacheNames = "Stats", key = "#year")
	public LottoResponse getYearlyStats(int year) {
		String index = year + "Stats";
		return lottoRepository.findBySubject(index)
				.map(LottoResponse::new)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
	}

	@Cacheable(cacheNames = "StatsIndex", key = "'allYearlyStatsIndex'")
	public YearMonthResponse getAllYearlyStatsIndex() {
		return getAllStatsIndexBySubject("yearlyStats");
	}

	private YearMonthResponse getAllStatsIndexBySubject(String subject) {
		List<Lotto> lottoList = lottoRepository.findAllBySubject(subject);
		if (lottoList.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		return new YearMonthResponse(lottoList.stream()
				.map(lotto -> {
					Object object;
					if (subject.equals("yearlyStats")) object = lotto.getCreationDate().getYear();
					else object = lotto.getCreationDate();

					return object.toString();
				})
				.sorted()
				.collect(Collectors.toList()));
	}

	private void saveLottoResult(String subject, Map<Integer, Integer> map, YearMonth yearMonth) {
		String result = manager.getTopNumbersAsString(map);
		List<Integer> countList = IntStream.rangeClosed(1, 45)
				.mapToObj(i -> map.getOrDefault(i, 0))
				.collect(Collectors.toList());

		Lotto lotto = new Lotto(subject, "Scheduler", yearMonth, countList, result);
		lottoRepository.save(lotto);
	}
}
