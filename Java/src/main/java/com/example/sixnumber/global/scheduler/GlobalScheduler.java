package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
import com.example.sixnumber.lotto.service.LottoService;
import com.example.sixnumber.lotto.service.WinNumberService;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("schedulerEnabled")
public class GlobalScheduler {

	private final UserRepository userRepository;
	private final LottoService lottoService;
	private final WinNumberRepository winNumberRepository;
	private final WinNumberService winNumberService;
	private final RedisTemplate<String, String> redisTemplate;
	private final Manager manager;

	@Scheduled(cron = "0 0 3 1 * ?")
	public void createMonthlyReportForPreviousMonth() {
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		lottoService.createMonthlyReport(lastMonth.getYear(), lastMonth.getMonthValue());
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

	@Scheduled(cron = "0 0 6 ? * SUN")
	public void updateLottoResultsOnSunday() {
		int nextRound = winNumberService.getFirstWinNumber().getTime() + 1;
		WinNumber winNumber = manager.retrieveLottoResult(nextRound)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		winNumberRepository.save(winNumber);

		List<WinNumber> winNumberList = adjustWinNumbers();
		winNumberService.updateCache(winNumberList);
		// winNumberList는 회차 내림차순
		winNumberService.updateCacheOfFirstWinNumber(winNumberList.get(0));
	}

	private List<WinNumber> adjustWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
		if (winNumberList.size() > 5) {
			List<WinNumber> remainingList = winNumberList.subList(5, winNumberList.size());
			winNumberRepository.deleteAll(remainingList);
			return winNumberList.subList(0, 5);
		} else return winNumberList;
	}
}