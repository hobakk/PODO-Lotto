package com.example.sixnumber.lotto.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
		// if (!lottoRepository.findBy()) {
		//
		// }

		Date today = new Date();
		SimpleDateFormat yd = new SimpleDateFormat("yyyy-MM");
		String targetDate = yd.format(today);
		String[] month = targetDate.split("-");
		System.out.println(month[1]+"월 가장 많이 나온 숫자 top6");
		// sixNumberRepository.
	}
}
