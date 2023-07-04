package com.example.sixnumber.lotto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class SixNumberServiceTest {
	@InjectMocks SixNumberService sixNumberService;

	@Mock
	private SixNumberRepository sixNumberRepository;
	@Mock
	private LottoRepository lottoRepository;
	@Mock
	private Manager manager;

	private Lotto lotto;
	private User saveUser;

	@BeforeEach
	public void setup() {
		lotto = mock(Lotto.class);
		saveUser = TestDataFactory.user();
	}

	@Test
	void buyNumber_success() {
		BuyNumberRequest buyNumberRequest = TestDataFactory.buyNumberRequest();

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		List<Integer> countList = TestDataFactory.countList();
		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		ListApiResponse<String> response = sixNumberService.buyNumber(buyNumberRequest, saveUser);

		verify(manager).findUser(anyLong());
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertNotNull(saveUser.getStatement());
		assertEquals(data.size(), 5);
		TestUtil.ListApiAssertEquals(response, 200, "요청 성공");
	}

	@Test
	void buyNumber_fail_lowCash() {
		BuyNumberRequest request = mock(BuyNumberRequest.class);
		when(request.getValue()).thenReturn(50);

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> sixNumberService.buyNumber(request, saveUser));

		verify(manager).findUser(anyLong());
	}

	@Test
	void statisticalNumber_success() {
		StatisticalNumberRequest request = TestDataFactory.statisticalNumberRequest();

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		List<Integer> countList = TestDataFactory.countList();
		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		ListApiResponse<String> response = sixNumberService.statisticalNumber(request, saveUser);

		verify(manager).findUser(anyLong());
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertNotNull(saveUser.getStatement());
		assertEquals(data.size(), 5);
		TestUtil.ListApiAssertEquals(response, 200, "요청 성공");
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#statisticalNumber")
	void statisticalNumber_fail_lowCash(int value, int repetition) {
		StatisticalNumberRequest request = mock(StatisticalNumberRequest.class);
		when(request.getValue()).thenReturn(value);
		when(request.getRepetition()).thenReturn(repetition);

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> sixNumberService.statisticalNumber(request, saveUser));

		verify(manager).findUser(anyLong());
	}
}
