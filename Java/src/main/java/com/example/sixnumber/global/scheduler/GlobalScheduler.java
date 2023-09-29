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
import com.example.sixnumber.user.entity.Statement;
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
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		Optional<Lotto> monthlyStats = lottoRepository.findByTopNumbersForMonth(lastMonth);
		if (monthlyStats.isEmpty()) {
			List<Integer> countList = new ArrayList<>();
			for (int i = 0; i < 45; i++) {
				countList.add(1);
			}

			List<SixNumber> lastMonthDateList = sixNumberRepository.findAllByBuyDate(lastMonth);
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
			Lotto lotto = new Lotto("Stats", "Scheduler", lastMonth, countList, result);
			lottoRepository.save(lotto);
		}
	}

	@Scheduled(cron = "0 0 9 * * *")
	public void paymentAndCancellation() {
		LocalDate now = LocalDate.now();
		List<User> userList = userRepository.findAllByRoleAndPaymentDate(UserRole.ROLE_PAID, now);

		for (User user : userList) {
			Boolean cancelPaid = user.getCancelPaid();
			int cash = user.getCash();
			if (cash >= 5000 && !cancelPaid || cancelPaid == null) {
				user.minusCash(5000);
				user.setPaymentDate(now.plusDays(31));
				user.addStatement(new Statement(user, "프리미엄 정기결제", 5000));
			} else {
				user.setRole(UserRole.ROLE_USER);
				user.setPaymentDate(null);
				user.setCancelPaid(null);
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
				if (!user.getRole().equals(UserRole.ROLE_ADMIN)) {
					user.setStatus(Status.SUSPENDED);
					String Key = "RT: " + user.getId();
					String refreshToken = redisTemplate.opsForValue().get(Key);
					if (refreshToken != null) redisTemplate.delete(refreshToken);
				}
			}
		}
	}
}
