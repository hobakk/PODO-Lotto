package com.example.sixnumber.lotto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SixNumberServiceTest {
	@InjectMocks SixNumberService sixNumberService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private SixNumberRepository sixNumberRepository;
	@Mock
	private LottoRepository lottoRepository;

	private Lotto lotto;
	private User saveUser;
	private SixNumber sixNumber;

	@BeforeEach
	public void setup() {
		lotto = mock(Lotto.class);
		sixNumber = mock(SixNumber.class);
		saveUser = TestDataFactory.user();
	}

	@Test
	void buyNumber_success() {
		BuyNumberRequest buyNumberRequest = TestDataFactory.buyNumberRequest();

		List<Integer> countList = TestDataFactory.countList();

		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		ListApiResponse<String> response = sixNumberService.buyNumber(buyNumberRequest, saveUser);

		verify(userRepository).save(any(User.class));
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertEquals(data.size(), 5);
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "요청 성공");
	}

	@Test
	void buyNumber_fail_lowCash() {
		BuyNumberRequest request = mock(BuyNumberRequest.class);
		when(request.getValue()).thenReturn(50);

		Assertions.assertThrows(IllegalArgumentException.class, () -> sixNumberService.buyNumber(request, saveUser));
	}

	@Test
	void statisticalNumber_success() {
		StatisticalNumberRequest request = TestDataFactory.statisticalNumberRequest();

		List<Integer> countList = TestDataFactory.countList();

		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		ListApiResponse<String> response = sixNumberService.statisticalNumber(request, saveUser);

		verify(userRepository).save(any(User.class));
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertEquals(data.size(), 5);
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "요청 성공");
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#statisticalNumber")
	void statisticalNumber_fail_lowCash(int value, int repetition) {
		StatisticalNumberRequest request = mock(StatisticalNumberRequest.class);
		when(request.getValue()).thenReturn(value);
		when(request.getRepetition()).thenReturn(repetition);

		Assertions.assertThrows(IllegalArgumentException.class, () -> sixNumberService.statisticalNumber(request, saveUser));
	}
}
