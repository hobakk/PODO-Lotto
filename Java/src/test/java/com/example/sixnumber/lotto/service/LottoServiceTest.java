package com.example.sixnumber.lotto.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;

@ExtendWith(MockitoExtension.class)
public class LottoServiceTest {
	@InjectMocks
	private LottoService lottoService;

	@Mock
	private LottoRepository lottoRepository;
	@Mock
	private Manager manager;

	private Lotto lotto;

	@BeforeEach
	public void setup() {
		lotto = mock(Lotto.class);
	}

	@Test
	void mainTopNumbers() {
		List<Integer> countList = Arrays.asList(4,5,6,7,8,9);
		when(lotto.getCountList()).thenReturn(countList);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		when(manager.revisedTopIndicesAsStr(anyList())).thenReturn("1 2 3 4 5 6");

		LottoResponse response = lottoService.mainTopNumbers();

		verify(lottoRepository).findByMain();
		verify(lotto).getCountList();
		verify(manager).revisedTopIndicesAsStr(anyList());
		assertEquals(response.getValue(), "1 2 3 4 5 6");
		assertEquals(response.getCountList(), countList);
	}

	@Test
	void mainTopNumber_fail() {
		when(lottoRepository.findByMain()).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> lottoService.mainTopNumbers());

		verify(lottoRepository).findByMain();
	}

	@Test
	void getTopNumberForMonth() {
		Lotto lotto = TestDataFactory.lotto();

		when(lottoRepository.findByTopNumbersForMonth(lotto.getCreationDate())).thenReturn(Optional.of(lotto));

		LottoResponse response = lottoService.getTopNumberForMonth(lotto.getCreationDate());

		verify(lottoRepository).findByTopNumbersForMonth(lotto.getCreationDate());
		assertEquals(response.getValue(), "1 2 3 4 5 6");
		assertEquals(response.getCountList(), TestDataFactory.countList());
	}

	@Test
	void getTopNumberForMonth_fail() {
		when(lottoRepository.findByTopNumbersForMonth(any(YearMonth.class))).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> lottoService.getTopNumberForMonth(YearMonth.now()));

		verify(lottoRepository).findByTopNumbersForMonth(any(YearMonth.class));
	}

	@Test
	void getAllMonthStats_success() {
		Lotto lotto = TestDataFactory.lotto();
		when(lottoRepository.findAllByMonthStats()).thenReturn(List.of(lotto));

		YearMonthResponse response = lottoService.getAllMonthStats();

		verify(lottoRepository).findAllByMonthStats();
		assertEquals(response.getYearMonthList(), List.of(lotto.getCreationDate().toString()));
	}

	@Test
	void getAllMonthStats_fail_isEmpty() {
		when(lottoRepository.findAllByMonthStats()).thenReturn(new ArrayList<>());

		Assertions.assertThrows(IllegalArgumentException.class, () -> lottoService.getAllMonthStats());

		verify(lottoRepository).findAllByMonthStats();
	}
}
