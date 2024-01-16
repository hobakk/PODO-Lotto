package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
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
	private final LottoRepository lottoRepository;
	private final SixNumberRepository sixNumberRepository;
	private final WinNumberRepository winNumberRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final Manager manager;

	@Scheduled(cron = "0 0 11 ? * SUN")
	public void findByTopNumberListForMonth() {
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		if (!lottoRepository.existsLottoByCreationDate(lastMonth)) {
			Map<Integer, Integer> map = new HashMap<>();

			sixNumberRepository.findAllByBuyDate(lastMonth.getYear(), lastMonth.getMonthValue()).forEach(sixNumber ->
				sixNumber.getNumberList().forEach(sentence ->
					Stream.of(sentence.split(" ")).forEach(topNumberStr -> {
						int key = Integer.parseInt(topNumberStr);
						map.put(key, map.getOrDefault(key, 0) + 1);
					})
				)
			);

			String result = manager.getTopNumbersAsString(map);

			List<Integer> countList = IntStream.rangeClosed(1, 45)
				.mapToObj(i -> map.getOrDefault(i, 0))
				.collect(Collectors.toList());

			Lotto lotto = new Lotto("Stats", "Scheduler", lastMonth, countList, result);
			lottoRepository.save(lotto);
		}
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
		Pageable pageable = PageRequest.of(0, 1);
		int newRound = winNumberRepository.findTopByTime(pageable).stream()
			.findFirst()
			.map(winNumber -> winNumber.getTime() + 1)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		
		WinNumber winNumber = manager.retrieveLottoResult(newRound)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		winNumberRepository.save(winNumber);
	}

	private List<WinNumber> adjustWinNumbers() {
		List<WinNumber> winNumberList = winNumberRepository.findAll();
		if (winNumberList.isEmpty()) throw new IllegalArgumentException("해당 정보가 존재하지 않습니다");

		if (winNumberList.size() > 5) {
			winNumberList.sort(Comparator.comparing(WinNumber::getTime).reversed());
			List<WinNumber> remainingList = winNumberList.subList(5, winNumberList.size());
			winNumberRepository.deleteAll(remainingList);
			return winNumberList.subList(0, 5);
		} else return winNumberList;
	}
}