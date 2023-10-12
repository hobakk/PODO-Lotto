package com.example.sixnumber.lotto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import org.springframework.data.domain.Pageable;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
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
	private SixNumber sixNumber;
	private User saveUser;

	@BeforeEach
	public void setup() {
		lotto = mock(Lotto.class);
		sixNumber = mock(SixNumber.class);
		saveUser = TestDataFactory.user();
	}

	@Test
	void buyNumber_success() {
		BuyNumberRequest buyNumberRequest = TestDataFactory.buyNumberRequest();

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		List<Integer> countList = TestDataFactory.countList();
		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		UnifiedResponse<List<String>> response = sixNumberService.buyNumber(buyNumberRequest, saveUser);

		verify(manager).findUser(anyLong());
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertNotNull(saveUser.getStatementList());
		assertEquals(data.size(), 5);
		TestUtil.UnifiedResponseListEquals(response, 200, "요청 성공");
	}

	@Test
	void buyNumber_fail_lowCash() {
		saveUser.minusCash(5000);

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

		UnifiedResponse<List<String>> response = sixNumberService.statisticalNumber(request, saveUser);

		verify(manager).findUser(anyLong());
		verify(lottoRepository).findByMain();
		verify(sixNumberRepository).save(any(SixNumber.class));
		List<String> data = response.getData();
		assertNotNull(saveUser.getStatementList());
		assertEquals(data.size(), 5);
		TestUtil.UnifiedResponseListEquals(response, 200, "요청 성공");
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

	@Test
	void getRecentBuyNumbers_success() {
		when(sixNumberRepository.findByRecentBuyNumbers(any(User.class), any(Pageable.class))).thenReturn(List.of(sixNumber));

		UnifiedResponse<List<String>> response = sixNumberService.getRecentBuyNumbers(saveUser);

		verify(sixNumberRepository).findByRecentBuyNumbers(any(User.class), any(Pageable.class));
		TestUtil.UnifiedResponseListEquals(response, 200, "최근 구매 번호 조회 성공");
	}

	@Test
	void getRecentBuyNumbers_fail_isEmpty() {
		when(sixNumberRepository.findByRecentBuyNumbers(any(User.class), any(Pageable.class))).thenReturn(new ArrayList<>());

		Assertions.assertThrows(CustomException.class, () -> sixNumberService.getRecentBuyNumbers(saveUser));

		verify(sixNumberRepository).findByRecentBuyNumbers(any(User.class), any(Pageable.class));
	}
}
