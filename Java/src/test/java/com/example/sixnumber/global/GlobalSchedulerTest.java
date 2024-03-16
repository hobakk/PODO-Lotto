package com.example.sixnumber.global;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.scheduler.GlobalScheduler;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
import com.example.sixnumber.lotto.service.WinNumberService;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class GlobalSchedulerTest {
	@InjectMocks
	private GlobalScheduler globalScheduler;

	@Mock
	private UserRepository userRepository;
	@Mock
	private LottoRepository lottoRepository;
	@Mock
	private SixNumberRepository sixNumberRepository;
	@Mock
	private WinNumberRepository winNumberRepository;
	@Mock
	private WinNumberService winNumberService;
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private Manager manager;

	private User saveUser;
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
		valueOperations = mock(ValueOperations.class);
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#cancellation")
	void paymentAndCancellation(User user, int resultCash, UserRole resultRole, LocalDate resultLocalDate, Boolean result) {
		when(userRepository.findAllByRoleAndPaymentDate(any(UserRole.class), any(LocalDate.class))).thenReturn(List.of(user));

		globalScheduler.paymentAndCancellation();

		verify(userRepository).findAllByRoleAndPaymentDate(any(UserRole.class), any(LocalDate.class));
		assertEquals(user.getCash(), resultCash);
		assertEquals(user.getRole(), resultRole);
		assertEquals(user.getPaymentDate(), resultLocalDate);
		assertEquals(user.getCancelPaid(), result);
	}

	@Test
	void withdrawExpiartion() {
		saveUser.setStatus(Status.DORMANT);
		saveUser.setWithdrawExpiration(LocalDate.now().minusMonths(2));

		when(userRepository.findByStatusAndWithdrawExpiration(eq(Status.DORMANT))).thenReturn(List.of(saveUser));

		globalScheduler.withdrawExpiration();

		verify(userRepository).findByStatusAndWithdrawExpiration(eq(Status.DORMANT));
		verify(userRepository).deleteAll(anyList());
	}

	@Test
	void autoSetSuspended() {
		saveUser.setTimeoutCount(4);

		when(userRepository.findUserByUntreatedAndRoleNot(anyInt(), any(UserRole.class))).thenReturn(List.of(saveUser));
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		globalScheduler.autoSetSuspended();

		verify(userRepository).findUserByUntreatedAndRoleNot(anyInt(), any(UserRole.class));
		verify(valueOperations).get(anyString());
		assertEquals(saveUser.getStatus(), Status.SUSPENDED);
	}

	@Test
	void updateLottoResultsOnSunday_success() {
		when(winNumberService.getFirstWinNumber()).thenReturn(TestDataFactory.winNumber());
		when(manager.retrieveLottoResult(1075)).thenReturn(Optional.of(TestDataFactory.winNumberRequest()));

		globalScheduler.updateLottoResultsOnSunday();

		verify(winNumberService).getFirstWinNumber();
		verify(manager).retrieveLottoResult(anyInt());
		verify(winNumberRepository).save(any(WinNumber.class));
		verify(winNumberService).updateCache(anyList());
		verify(winNumberService).updateCacheOfFirstWinNumber(any(WinNumber.class));
	}
}