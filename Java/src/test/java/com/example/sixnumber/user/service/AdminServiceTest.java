package com.example.sixnumber.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
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

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private LottoRepository lottoRepository;
	@Mock
	private RedisDao redisDao;
	@Mock
	private Manager manager;
	@Mock
	private RedisTemplate<String, String> redisTemplate;

	private User saveUser;
	private User admin;

	@BeforeEach
	public void setup() {
		// MockitoAnnotations.openMocks(this);
		saveUser = TestDataFactory.user();
		admin = TestDataFactory.Admin();
	}

	@Test
	void setAdmin_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("AdminSecurityKey");

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		ApiResponse response = adminService.setAdmin(request, admin, saveUser.getId());

		verify(manager).findUser(anyLong());
		assertEquals(saveUser.getRole(), UserRole.ROLE_ADMIN);
		TestUtil.ApiAsserEquals(response, 200, "변경 완료");
	}

	@Test
	void setAdmin_fail_incorrectKey() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.setAdmin(request, admin, saveUser.getId()));
	}
	// setAdmin confirmationProcess 에 대한 실패 test code 는 setStatus 에서 검증해서 불필요

	@Test
	void getUsers() {
		when(userRepository.findAll()).thenReturn(List.of(saveUser));

		ListApiResponse<UsersReponse> response = adminService.getUsers();

		verify(userRepository).findAll();
		TestUtil.ListApiAssertEquals(response, 200, "조회 성공");
	}

	@Test
	void getCharges() {
		List<String> values = TestDataFactory.values();

		when(redisDao.multiGet(anyString())).thenReturn(values);

		ListApiResponse<AdminGetChargingResponse> response = adminService.getChargs();

		verify(redisDao).multiGet(anyString());
		assertEquals(response.getData().size(), 3);
		TestUtil.ListApiAssertEquals(response, 200, "조회 성공");
	}

	@Test
	void searchCharging_success() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		List<String> values = Collections.singletonList("7-Value1-5000");

		when(redisDao.multiGet(anyString())).thenReturn(values);

		ItemApiResponse<AdminGetChargingResponse> response = adminService.searchCharging(request);

		verify(redisDao).multiGet(anyString());
		TestUtil.ItemApiAssertEquals(response, 200, "조회 성공");
	}

	@Test
	void upCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		ApiResponse response = adminService.upCash(request);

		verify(manager).findUser(anyLong());
		verify(redisDao).deleteValues(anyString());
		assertEquals(saveUser.getCash(), 11000);
		assertNotNull(saveUser.getStatement().get(0));
		assertEquals(saveUser.getChargingCount(), 0);
		TestUtil.ApiAsserEquals(response, 200, "충전 완료");
	}

	@Test
	void downCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		ApiResponse response = adminService.downCash(request);

		verify(manager).findUser(anyLong());
		assertEquals(saveUser.getCash(), 1000);
		assertNotNull(saveUser.getStatement().get(0));
		TestUtil.ApiAsserEquals(response, 200, "차감 완료");
	}

	@Test
	void downCash_fail_manyValue() {
		CashRequest request = mock(CashRequest.class);
		when(request.getValue()).thenReturn(10000);

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.downCash(request));

		verify(manager).findUser(anyLong());
	}

	@Test
	void createLotto_success() {
		Optional<Lotto> empty = Optional.empty();
		when(lottoRepository.findByMain()).thenReturn(empty);

		ApiResponse response = adminService.createLotto("email");

		verify(lottoRepository).findByMain();
		verify(lottoRepository).save(any(Lotto.class));
		TestUtil.ApiAsserEquals(response, 200, "생성 완료");
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

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		ApiResponse response = adminService.setStatus(admin, saveUser.getId(), request);

		verify(manager).findUser(anyLong());
		assertEquals(saveUser.getStatus(), Status.ACTIVE);
		TestUtil.ApiAsserEquals(response, 200, "상태 변경 완료");
	}

	@ParameterizedTest
	@ValueSource(strings = {"SUSPENDED", "DORMANT"})
	void setStatus_success_suspended_or_dormant(String statusStr) {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn(statusStr);

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		// setStatus_success_active 에서 redisTemlate 값이 없을 때 검증되서 값이 있을 경우만 검증함
		when(redisDao.deleteIfNotNull(anyString()));

		ApiResponse response = adminService.setStatus(admin, saveUser.getId(), request);

		verify(manager).findUser(anyLong());
		verify(redisDao).deleteIfNotNull(anyString());
		assertEquals(saveUser.getStatus(), Status.valueOf(statusStr));
		TestUtil.ApiAsserEquals(response, 200, "상태 변경 완료");
	}

	@Test
	void setStatus_fail_incorrectMsg() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		Assertions.assertThrows(
			CustomException.class, () -> adminService.setStatus(admin, saveUser.getId(), request));

		verify(manager).findUser(anyLong());
	}

	@Test
	void setStatus_fail_overlapStatus() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("ACTIVE");

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.setStatus(admin, saveUser.getId(), request));

		verify(manager).findUser(anyLong());
	}
}
