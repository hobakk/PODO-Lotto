package com.example.sixnumber.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private CashRepository cashRepository;
	@Mock
	private LottoResponse lottoResponse;
	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@BeforeEach
	public void setup() {
		// MockitoAnnotations.openMocks(this);
	}

	@Test
	void setAdmin() {
		User admin = mock(User.class);
		when(admin.getId()).thenReturn(1L);

		User target = mock(User.class);
		when(target.getId()).thenReturn(99L);
		when(target.getRole()).thenReturn(UserRole.ROLE_USER);

		when(userRepository.findById(target.getId())).thenReturn(Optional.of(target));

		ApiResponse response = adminService.setAdmin(admin, target.getId());

		verify(target).setAdmin();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "변경 완료");
	}

	@Test
	void getUsers() {
		User user = TestDataFactory.user();

		when(userRepository.findAll()).thenReturn(List.of(user));

		ListApiResponse<UsersReponse> response = adminService.getUsers();

		verify(userRepository).findAll();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void getAfterChargs() {
		Cash cash = TestDataFactory.cash();
		cash.setProcessingAfter();

		when(cashRepository.processingEqaulAfter()).thenReturn(List.of(cash));

		ListApiResponse<Cash> response = adminService.getAfterChargs();

		verify(cashRepository).processingEqaulAfter();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void getBeforeChargs() {
		Cash cash = TestDataFactory.cash();

		when(cashRepository.processingEqaulBefore()).thenReturn(List.of(cash));

		ListApiResponse<Cash> response = adminService.getBeforeChargs();

		verify(cashRepository).processingEqaulBefore();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void upCash() {
		CashRequest request = TestDataFactory.cashRequest();

		User user = mock(User.class);
		when(user.getId()).thenReturn(7L);

		Cash cash = mock(Cash.class);
		when(cash.getUserId()).thenReturn(7L);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(cashRepository.findById(anyLong())).thenReturn(Optional.of(cash));

		ApiResponse response = adminService.upCash(request);

		verify(userRepository).findById(anyLong());
		verify(cashRepository).findById(anyLong());
		verify(user).setCash("+", request.getValue());
		verify(cash).setProcessingAfter();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "충전 완료");
	}

	@Test
	void downCash() {
		CashRequest request = TestDataFactory.cashRequest();

		User user = TestDataFactory.user();
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		ApiResponse response = adminService.downCash(request);

		verify(userRepository).findById(anyLong());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "차감 완료");
	}
}
