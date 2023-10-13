package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.entity.Lotto;
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
		lottoRepository.findByTopNumbersForMonth(lastMonth).ifPresentOrElse(
			lastMonthStats -> {},
			() -> {
				List<Integer> countList = new ArrayList<>(Collections.nCopies(45, 1));

				sixNumberRepository.findAllByBuyDate(lastMonth).forEach(sixNumber ->
					sixNumber.getNumberList().forEach(sentence ->
						Stream.of(sentence.split(" ")).forEach(topNumberStr -> {
							int topNum = Integer.parseInt(topNumberStr);
							countList.set(topNum, countList.get(topNum) + 1);
						})
					)
				);

				String result = manager.revisedTopIndicesAsStr(countList);
				Lotto lotto = new Lotto("Stats", "Scheduler", lastMonth, countList, result);
				lottoRepository.save(lotto);
			}
		);
	}

	@Scheduled(cron = "0 0 9 * * *")
	public void paymentAndCancellation() {
		userRepository.findAllByRoleAndPaymentDate(UserRole.ROLE_PAID, LocalDate.now())
			.forEach(user -> {
				Boolean cancelPaid = user.getCancelPaid();
				int cash = user.getCash();

				if (cash >= 5000 && !cancelPaid || cancelPaid == null) user.monthlyPayment();
				else user.changeToROLE_USER();
			});
	}

	@Scheduled(cron = "0 0 7 ? * MON-FRI")
	public void	withdrawExpiration() {
		List<User> withdrawList = userRepository.findByStatusAndWithdrawExpiration(Status.DORMANT);

		if (!withdrawList.isEmpty()) userRepository.deleteAll(withdrawList);
	}

	@Scheduled(cron = "0 0 6,18 * * *")
	public void	autoSetSuspended() {
		userRepository.findUserByUntreatedAndRoleNot(4, UserRole.ROLE_ADMIN)
			.forEach(user -> {
				user.setStatus(Status.SUSPENDED);
				String Key = "RT: " + user.getId();
				String refreshToken = redisTemplate.opsForValue().get(Key);
				if (refreshToken != null) redisTemplate.delete(refreshToken);
			});
	}
}
