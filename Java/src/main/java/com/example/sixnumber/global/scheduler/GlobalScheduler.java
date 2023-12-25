package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.exception.OverlapException;
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
@Transactional
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

		if (winNumberRepository.existsWinNumberByTime(newRound))
			throw new OverlapException("이미 등록된 당첨 결과 입니다");
		
		WinNumber winNumber = manager.retrieveLottoResult(newRound)
			.map(WinNumber::new)
			.orElseThrow(() -> new IllegalArgumentException("해당 회차의 정보가 없습니다"));

		winNumberRepository.save(winNumber);
	}
}