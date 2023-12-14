package com.example.sixnumber.lotto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.lotto.repository.WinNumberRepository;
import com.example.sixnumber.lotto.service.WinNumberService;

@ExtendWith(MockitoExtension.class)
public class WinNumberServiceTest {
	@InjectMocks
	WinNumberService winNumberService;

	@Mock
	private WinNumberRepository winNumberRepository;

	private WinNumber winNumber;

	@BeforeEach
	public void setup() {
		winNumber = TestDataFactory.winNumber();
	}

	@Test
	void getWinNumber_success() {
		List<WinNumber> winNumberList = new ArrayList<>();
		winNumberList.add(winNumber);

		when(winNumberRepository.findAll()).thenReturn(winNumberList);

		WinNumberResponse response = winNumberService.getWinNumbers();

		verify(winNumberRepository).findAll();
		assertEquals(response.getWinNumberList().size(), 1);
	}

	@Test
	void getWinNumber_fail_isEmpty() {
		List<WinNumber> listIsEmpty = new ArrayList<>();

		when(winNumberRepository.findAll()).thenReturn(listIsEmpty);

		Assertions.assertThrows(IllegalArgumentException.class, () -> winNumberService.getWinNumbers());

		verify(winNumberRepository).findAll();
	}

	@Test
	void setWinNumbers_success() {
		List<WinNumber> winNumberList = new ArrayList<>();
		winNumberList.add(winNumber);

		when(winNumberRepository.existsWinNumberByTime(anyInt())).thenReturn(false);
		when(winNumberRepository.findAll()).thenReturn(winNumberList);

		WinNumberResponse response = winNumberService.setWinNumbers(1075);

		verify(winNumberRepository).existsWinNumberByTime(anyInt());
		verify(winNumberRepository).save(any(WinNumber.class));
		verify(winNumberRepository).findAll();
		assertEquals(response.getWinNumberList().size(), 1);
	}

	@Test
	void setWinNumber_fail_overLap() {
		WinNumberRequest request = TestDataFactory.winNumberRequest();

		when(winNumberRepository.existsWinNumberByTime(anyInt())).thenReturn(true);

		Assertions.assertThrows(OverlapException.class, () -> winNumberService.setWinNumbers(1075));

		verify(winNumberRepository).existsWinNumberByTime(anyInt());
	}
}
