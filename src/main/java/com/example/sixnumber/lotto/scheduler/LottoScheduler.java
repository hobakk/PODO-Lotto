package com.example.sixnumber.lotto.scheduler;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class LottoScheduler {

	private final LottoService lottoService;
	private final LottoRepository lottoRepository;
	private final SixNumberRepository sixNumberRepository;

	@Scheduled(cron = "0 0 0 0 * ?")
	public void findByTopNumberListForMonth() {

		String ym = YearMonth.now().toString();
		String[] yToM = ym.split("-");
		int year = Integer.parseInt(yToM[0]);
		int lastMonth = Integer.parseInt(yToM[1]) - 1;
		YearMonth findYm = YearMonth.of(year, lastMonth);
		List<Lotto> lottoList = lottoRepository.findByTopNubersForMonth(findYm);

		if (lottoList.isEmpty()) {
			System.out.println(lastMonth+"월 통계 생성중");
			List<Integer> countList = new ArrayList<>();
			for (int i = 0; i < 45; i++) {
				countList.add(1);
			}

			List<SixNumber> monthDataList = sixNumberRepository.findAllByBuyDate(year, lastMonth);
		}
	}
}
