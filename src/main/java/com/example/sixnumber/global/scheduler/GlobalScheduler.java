package com.example.sixnumber.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Scheduled(cron = "0 0 11 ? * MON-FRI")
	public void findByTopNumberListForMonth() {
		String[] ym = YearMonth.now().minusMonths(1).toString().split("-");
		int year = Integer.parseInt(ym[0]);
		int lastMonth = Integer.parseInt(ym[1]);
		YearMonth yLastMonth = YearMonth.of(year, lastMonth);

		System.out.println(lastMonth + "월 통계 조회");
		Optional<Lotto> lotto = lottoRepository.findByTopNumbersForMonth(yLastMonth);

		if (lotto.isEmpty()) {
			generatesStatistics(year, lastMonth, yLastMonth);
		}
	}

	// 월 말에 월정액 가입한 유저의 경우 몇일 지나고 다시 결제가 되는데 어떻게 처리할지 고민해야함 예: YearMonth -> LocalDate
	@Scheduled(cron = "0 0 9 ? * MON-FRI")
	public void paymentAndCancellation() {
		System.out.println("자동 결제 및 해지");
		String lastMonth = YearMonth.now().minusMonths(1).toString();

		List<User> userList = userRepository.findByRole(UserRole.ROLE_PAID);
		for (User user : userList) {
			String paymentDate = user.getPaymentDate();

			if (paymentDate.equals(lastMonth) && user.getCash() >= 5000 && !user.getPaymentDate().equals("월정액 해지")) {
				user.setCash("-", 5000);
				user.setPaymentDate(YearMonth.now().toString());
				user.setStatement(LocalDate.now() + "," + YearMonth.now() + "월 정액 비용 5000원 차감");
			} else if (paymentDate.equals("월정액 해지") || user.getCash() < 5000) {
				user.setRole("USER");
				user.setPaymentDate("");
			} else {
				throw new IllegalArgumentException("얘기치 않은 동작 및 오류");
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
			statistics = statistics + "(" + (i+1) + " : " + countList.get(i) + "), ";
		}
		statistics = statistics.substring(0, statistics.length() -2);

		List<Integer> sortedIndices = new ArrayList<>();
		for (int i = 0; i < countList.size(); i++) {
			sortedIndices.add(i);
		}

		// 중복코드를 어떻게 처리할지 고민해봐야함
		sortedIndices.sort((index1, index2) -> countList.get(index2).compareTo(countList.get(index1)));
		List<Integer> topIndices = sortedIndices.subList(0, Math.min(sortedIndices.size(), 6));
		Collections.sort(topIndices);
		topIndices.replaceAll(Integer -> Integer + 1);
		String value = topIndices.stream().map(Object::toString).collect(Collectors.joining(" "));

		// 새로운 db에 statistics, value 를 저장하는게 좋을지 고민해봐야함
		Lotto lotto = new Lotto(lastMonth + "월 통계", "Scheduler", findYm, countList, statistics, value);
		lottoRepository.save(lotto);
	}
}
