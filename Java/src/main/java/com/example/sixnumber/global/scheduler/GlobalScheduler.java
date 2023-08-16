package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class GlobalScheduler {

	private final UserRepository userRepository;
	private final LottoRepository lottoRepository;
	private final SixNumberRepository sixNumberRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final Manager manager;

	@Scheduled(cron = "0 0 11 ? * MON-FRI")
	public void findByTopNumberListForMonth() {
		String[] ym = YearMonth.now().minusMonths(1).toString().split("-");
		int year = Integer.parseInt(ym[0]);
		int lastMonth = Integer.parseInt(ym[1]);
		YearMonth yLastMonth = YearMonth.of(year, lastMonth);

		Optional<Lotto> lastMonthLotto = lottoRepository.findByTopNumbersForMonth(yLastMonth);

		if (lastMonthLotto.isEmpty()) {
			List<Integer> countList = new ArrayList<>();
			for (int i = 0; i < 45; i++) { countList.add(1); }

			List<SixNumber> lastMonthDateList = sixNumberRepository.findAllByBuyDate(year, lastMonth);
			for (SixNumber sixNumber : lastMonthDateList) {
				List<String> topNumberList = sixNumber.getNumberList();
				for (String sentence : topNumberList) {
					String[] topNumbers = sentence.split(" ");
					for (String topNumber : topNumbers) {
						int num = Integer.parseInt(topNumber);
						countList.set(num, countList.get(num) + 1);
					}
				}
			}

			String result = manager.revisedTopIndicesAsStr(countList);
			Lotto lotto = new Lotto(lastMonth + " Stats", "Scheduler", yLastMonth, countList, result);
			lottoRepository.save(lotto);
		}
	}

	@Scheduled(cron = "0 0 9 * * *")
	public void paymentAndCancellation() {
		LocalDate now = LocalDate.now();
		List<User> userList = userRepository.findAllByRoleAndPaymentDate(UserRole.ROLE_PAID, now.toString());

		for (User user : userList) {
			String paymentDate = user.getPaymentDate();
			int cash = user.getCash();
			if (cash >= 5000 && !paymentDate.equals("월정액 해지")) {
				user.setCash("-", 5000);
				user.setPaymentDate(now.plusDays(31).toString());
				user.setStatement(LocalDate.now() + "," + YearMonth.now() + "월 정액 비용 5000원 차감");
			} else {
				user.setRole(UserRole.ROLE_USER);
				user.setPaymentDate("");
			}
		}
	}

	@Scheduled(cron = "0 0 7 ? * MON-FRI")
	public void	withdrawExpiration() {
		System.out.println("탈퇴한 유저 정보 보유기간 만료 확인");
		List<User> withdrawList = userRepository.findByStatusAndWithdrawExpiration(Status.DORMANT);
		if (!withdrawList.isEmpty()) {
			userRepository.deleteAll(withdrawList);
		}
	}

	@Scheduled(cron = "0 0 6,18 * * *")
	public void	autoSetSuspended() {
		System.out.println("미처리 누적에 대한 정지 처리");
		List<User> untreatedUsers = userRepository.findUserByUntreated(4);
		if (!untreatedUsers.isEmpty()) {
			for (User user : untreatedUsers) {
				user.setStatus("SUSPENDED");
				String Key = "RT: " + user.getId();
				String refreshToken = redisTemplate.opsForValue().get(Key);
				if (refreshToken != null) redisTemplate.delete(refreshToken);
			}
		}
	}
}
