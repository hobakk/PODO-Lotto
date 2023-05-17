package com.example.sixnumber.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private CashRepository cashRepository;
	@Mock
	private LottoRepository lottoRepository;
	@Mock
	private RedisTemplate<String, String> redisTemplate;

	private ValueOperations<String, String> valueOperations;
	private User saveUser;
	private User admin;
	private Cash saveCash;

	@BeforeEach
	public void setup() {
		// MockitoAnnotations.openMocks(this);
		valueOperations = mock(ValueOperations.class);
		saveUser = TestDataFactory.user();
		admin = TestDataFactory.Admin();
		saveCash = TestDataFactory.cash();
	}

	@Test
	void setAdmin_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("AdminSecurityKey");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		ApiResponse response = adminService.setAdmin(request, admin, saveUser.getId());

		verify(userRepository).findById(anyLong());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "변경 완료");
	}

	@Test
	void setAdmin_fail_incorrectKey() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.setAdmin(request, admin, saveUser.getId()));
	}

	@Test
	void getUsers() {
		when(userRepository.findAll()).thenReturn(List.of(saveUser));

		ListApiResponse<UsersReponse> response = adminService.getUsers();

		verify(userRepository).findAll();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void getAfterChargs() {
		saveCash.setProcessingAfter();

		when(cashRepository.processingEqaulAfter()).thenReturn(List.of(saveCash));

		ListApiResponse<Cash> response = adminService.getAfterChargs();

		verify(cashRepository).processingEqaulAfter();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void getBeforeChargs() {
		when(cashRepository.processingEqaulBefore()).thenReturn(List.of(saveCash));

		ListApiResponse<Cash> response = adminService.getBeforeChargs();

		verify(cashRepository).processingEqaulBefore();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "조회 성공");
	}

	@Test
	void upCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));
		when(cashRepository.findById(anyLong())).thenReturn(Optional.of(saveCash));

		ApiResponse response = adminService.upCash(request);

		verify(userRepository).findById(anyLong());
		verify(cashRepository).findById(anyLong());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "충전 완료");
	}

	@Test
	void upCash_fail_incorrectUser() {
		CashRequest request = mock(CashRequest.class);
		when(request.getUserId()).thenReturn(4L);

		Cash cash = mock(Cash.class);
		when(cash.getUserId()).thenReturn(4L);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));
		when(cashRepository.findById(anyLong())).thenReturn(Optional.of(cash));

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.upCash(request));

		verify(userRepository).findById(anyLong());
		verify(cashRepository).findById(anyLong());
	}

	@Test
	void downCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		ApiResponse response = adminService.downCash(request);

		verify(userRepository).findById(anyLong());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "차감 완료");
	}

	@Test
	void downCash_fail_manyValue() {
		CashRequest request = mock(CashRequest.class);
		when(request.getValue()).thenReturn(10000);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.downCash(request));

		verify(userRepository).findById(anyLong());
	}

	@Test
	void createLotto_success() {
		Optional<Lotto> empty = Optional.empty();
		when(lottoRepository.findByMain()).thenReturn(empty);

		ApiResponse response = adminService.createLotto("email");

		verify(lottoRepository).findByMain();
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "생성 완료");
	}

	@Test
	void createLotto_fail_overlapLotto() {
		Lotto lotto = mock(Lotto.class);

		when(lottoRepository.findByMain()).thenReturn(Optional.of(lotto));

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.createLotto(admin.getEmail()));

		verify(lottoRepository).findByMain();
	}

	@Test
	void setStatus_success_active() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("ACTIVE");

		saveUser.setStatus("SUSPENDED");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		ApiResponse response = adminService.setStatus(admin, saveUser.getId(), request);

		assertEquals(saveUser.getStatus(), Status.ACTIVE);
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "상태 변경 완료");
	}

	@ParameterizedTest
	@ValueSource(strings = {"SUSPENDED", "DORMANT"})
	void setStatus_success_suspended_or_dormant(String statusStr) {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn(statusStr);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		// setStatus_success_active 에서 redisTemlate 값이 없을 때 검증되서 값이 있을 경우만 검증함
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn("RTV");

		ApiResponse response = adminService.setStatus(admin, saveUser.getId(), request);

		verify(redisTemplate).delete(anyString());
		assertEquals(saveUser.getStatus(), Status.valueOf(statusStr));
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "상태 변경 완료");
	}

	@Test
	void setStatus_fail_incorrectMsg() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.setStatus(admin, saveUser.getId(), request));

		verify(userRepository).findById(anyLong());
	}

	@Test
	void setStatus_fail_overlapStatus() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("ACTIVE");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.setStatus(admin, saveUser.getId(), request));

		verify(userRepository).findById(anyLong());
	}

}
