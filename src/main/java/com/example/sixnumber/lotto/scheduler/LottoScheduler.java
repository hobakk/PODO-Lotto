package com.example.sixnumber.lotto.scheduler;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class LottoScheduler {

	private final LottoRepository lottoRepository;
	private final SixNumberRepository sixNumberRepository;

	@Scheduled(cron = "0 0 11 ? * MON-FRI *")
	public void findByTopNumberListForMonth() {

		String ym = YearMonth.now().toString();
		String[] yToM = ym.split("-");
		int year = Integer.parseInt(yToM[0]);
		int lastMonth = Integer.parseInt(yToM[1]) - 1;

		System.out.println(lastMonth+"월 통계 조회");
		YearMonth findYm = YearMonth.of(year, lastMonth);
		Optional<Lotto> lotto = lottoRepository.findByTopNumbersForMonth(findYm);

		if (lotto.isEmpty()) {
			generatesStatistics(year, lastMonth, findYm);
		}
	}

	// 너무 많은 작업을 담당하기에 분리함
	private void generatesStatistics(int year, int lastMonth, YearMonth findYm) {
		String statistics = "";
		System.out.println(lastMonth+"월 통계 생성중");
		List<Integer> countList = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			countList.add(1);
		}

		List<SixNumber> monthDated = sixNumberRepository.findAllByBuyDate(year, lastMonth);
		for (SixNumber sixNumber : monthDated) {
			List<String> topNumberList = sixNumber.getNumberList();
			for (String sentence : topNumberList) {
				String[] topNumbers = sentence.split(" ");
				for (String topNumber : topNumbers) {
					int num = Integer.parseInt(topNumber);
					countList.set(num, countList.get(num) + 1);
				}
			}
		}
		for (int i = 0; i < countList.size(); i++) {
			statistics = "(" + i+1 + " : " + countList.get(i) + "), ";
			if (i % 9 == 0) {
				statistics = statistics + "\n";
			}
		}

		List<Integer> sortedIndices = new ArrayList<>();
		for (int i = 0; i < countList.size(); i++) {
			sortedIndices.add(i);
		}

		sortedIndices.sort((index1, index2) -> countList.get(index2).compareTo(countList.get(index1)));
		List<Integer> topIndices = sortedIndices.subList(0, Math.min(sortedIndices.size(), 6));
		Collections.sort(topIndices);
		String value = topIndices.stream().map(Object::toString).collect(Collectors.joining(" "));

		// 새로운 db에 statistics, value 를 저장하는게 좋을지 고민해봐야함
		Lotto lotto = new Lotto(lastMonth + "월 통계", "Scheduler", findYm, countList, statistics, value);
		lottoRepository.save(lotto);
	}
}
