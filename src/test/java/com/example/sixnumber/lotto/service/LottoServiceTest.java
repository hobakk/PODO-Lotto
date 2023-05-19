package com.example.sixnumber.lotto.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;

@ExtendWith(MockitoExtension.class)
public class LottoServiceTest {
	@InjectMocks
	private LottoService lottoService;

	@Mock
	private LottoRepository lottoRepository;

	private Lotto lotto;

	@BeforeEach
	public void setup() {
		lotto = mock(Lotto.class);
	}

	@Test
	void mainTopNimbers() {
		when(lotto.getCountList()).thenReturn(Arrays.asList(4,5,6,7,8,9));

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		ItemApiResponse<LottoResponse> response = lottoService.mainTopNumbers();

		verify(lottoRepository).findByMain();
		verify(lotto).getCountList();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
		LottoResponse data = response.getData();
		assertNotNull(data);
		assertEquals(data.getStatistics(), "(1번 : 4), (2번 : 5), (3번 : 6), (4번 : 7), (5번 : 8), (6번 : 9)");
		assertEquals(data.getValue(), "1 2 3 4 5 6");
	}

	@Test
	void getTopNumberForMonth() {
		YearMonthRequest request = mock(YearMonthRequest.class);
		when(request.getYearMonth()).thenReturn(YearMonth.now());

		when(lottoRepository.findByTopNumbersForMonth(request.getYearMonth())).thenReturn(Optional.of(lotto));

		ItemApiResponse<LottoResponse> response = lottoService.getTopNumberForMonth(request);

		verify(lottoRepository).findByTopNumbersForMonth(request.getYearMonth());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
		LottoResponse data = response.getData();
		assertNotNull(data);
	}
}
