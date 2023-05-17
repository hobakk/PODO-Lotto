package com.example.sixnumber.global;

import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.scheduler.GlobalScheduler;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
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

	private User saveUser;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
	}

	@Test
	void findByTopNumberListForMonth() {
		SixNumber sixNumber = mock(SixNumber.class);

		when(sixNumber.getNumberList()).thenReturn(Arrays.asList("1 2 3", "4 5 6"));
		when(sixNumberRepository.findAllByBuyDate(anyInt(), anyInt())).thenReturn(List.of(sixNumber));

		globalScheduler.findByTopNumberListForMonth();

		verify(sixNumber).getNumberList();
		verify(sixNumberRepository).findAllByBuyDate(anyInt(), anyInt());
		verify(lottoRepository).save(any(Lotto.class));
	}

	@Test
	void payment() {
		saveUser.setStatus("PAID");
		saveUser.setPaymentDate(String.valueOf(YearMonth.of(2023, 4)));

		when(userRepository.findByRole(UserRole.ROLE_PAID)).thenReturn(List.of(saveUser));

		globalScheduler.paymentAndCancellation();

		verify(userRepository).findByRole(UserRole.ROLE_PAID);
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#cancellation")
	void Cancellation(String yearMonth, String sign, int cash) {
		saveUser.setStatus("PAID");
		saveUser.setPaymentDate(yearMonth);
		saveUser.setCash(sign, cash);

		when(userRepository.findByRole(UserRole.ROLE_PAID)).thenReturn(List.of(saveUser));

		globalScheduler.paymentAndCancellation();

		verify(userRepository).findByRole(UserRole.ROLE_PAID);
	}

	@Test
	void paymentAndCancellation_fail() {
		saveUser.setStatus("PAID");
		saveUser.setPaymentDate("false");

		when(userRepository.findByRole(UserRole.ROLE_PAID)).thenReturn(List.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class, () -> globalScheduler.paymentAndCancellation());

		verify(userRepository).findByRole(UserRole.ROLE_PAID);
	}
}
