package com.example.sixnumber.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.service.AdminService;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private RedisDao redisDao;

	private User saveUser;
	private User admin;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
		admin = TestDataFactory.Admin();
	}

	@Test
	void getUsers() {
		when(userRepository.findAll()).thenReturn(List.of(saveUser));

		UnifiedResponse<List<UserResponse>> response = adminService.getUsers();

		verify(userRepository).findAll();
		TestUtil.UnifiedResponseListEquals(response, 200, "조회 성공");
	}

	@Test
	void getCharges() {
		List<String> values = List.of(TestDataFactory.chargeKey());

		when(redisDao.multiGet(anyString())).thenReturn(values);

		UnifiedResponse<List<AdminGetChargingResponse>> response = adminService.getCharges();

		verify(redisDao).multiGet(anyString());
		assertEquals(response.getData().size(), 1);
		assertEquals(response.getData().get(0).getDate(), "12시 30분");
		TestUtil.UnifiedResponseListEquals(response, 200, "조회 성공");
	}

	@Test
	void searchCharging_success() {
		String msg = "Value1";
		int cash = 5000;

		List<String> values = Collections.singletonList("7-Value1-5000-7월 14일");

		when(redisDao.multiGet(anyString())).thenReturn(values);

		UnifiedResponse<AdminGetChargingResponse> response = adminService.searchCharging(msg, cash);

		verify(redisDao).multiGet(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공", AdminGetChargingResponse.class);
	}

	@Test
	void upCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = adminService.upCash(request);

		verify(userRepository).findById(anyLong());
		verify(redisDao).delete(anyString());
		assertEquals(saveUser.getCash(), 11000);
		assertNotNull(saveUser.getStatementList());
		assertEquals(saveUser.getTimeoutCount(), 0);
		TestUtil.UnifiedResponseEquals(response, 200, "충전 완료");
	}

	@Test
	void downCash_success() {
		CashRequest request = TestDataFactory.cashRequest();

		when(userRepository.findByIdAndCashGreaterThanEqual(anyLong(), anyInt()))
			.thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = adminService.downCash(request);

		verify(userRepository).findByIdAndCashGreaterThanEqual(anyLong(), anyInt());
		assertEquals(saveUser.getCash(), 1000);
		assertNotNull(saveUser.getStatementList().get(0));
		TestUtil.UnifiedResponseEquals(response, 200, "차감 완료");
	}

	@Test
	void downCash_fail_manyValue() {
		CashRequest request = mock(CashRequest.class);

		when(userRepository.findByIdAndCashGreaterThanEqual(anyLong(), anyInt()))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> adminService.downCash(request));

		verify(userRepository).findByIdAndCashGreaterThanEqual(anyLong(), anyInt());
	}

	@Test
	void setStatus_success_active() {
		OnlyMsgRequest request = new OnlyMsgRequest("ACTIVE");

		saveUser.setStatus(Status.SUSPENDED);

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = adminService.setStatus(saveUser.getId(), request);

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
		verify(userRepository).save(any(User.class));
		assertEquals(saveUser.getStatus(), Status.ACTIVE);
		TestUtil.UnifiedResponseEquals(response, 200, "상태 변경 완료");
	}

	@ParameterizedTest
	@ValueSource(strings = {"SUSPENDED", "DORMANT"})
	void setStatus_success_suspended_or_dormant(String statusStr) {
		OnlyMsgRequest request = new OnlyMsgRequest(statusStr);

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = adminService.setStatus(saveUser.getId(), request);

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
		verify(redisDao).delete(anyString());
		verify(userRepository).save(any(User.class));
		TestUtil.UnifiedResponseEquals(response, 200, "상태 변경 완료");
	}

	@Test
	void setStatus_fail_incorrectMsg() {
		OnlyMsgRequest request = new OnlyMsgRequest("false");

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(
			CustomException.class, () -> adminService.setStatus(saveUser.getId(), request));

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
	}

	@Test
	void setStatus_fail_overlapStatus() {
		OnlyMsgRequest request = new OnlyMsgRequest("ACTIVE");

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> adminService.setStatus(saveUser.getId(), request));

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
	}

	@Test
	void setRole_success() {
		OnlyMsgRequest request = new OnlyMsgRequest("PAID");

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = adminService.setRole(saveUser.getId(), request);

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
		verify(userRepository).save(any(User.class));
		TestUtil.UnifiedResponseEquals(response, 200, "권한 변경 완료");
	}

	@Test
	void setRole_fail_incorrect() {
		OnlyMsgRequest request = new OnlyMsgRequest("USER");

		when(userRepository.findByIdAndRoleNot(anyLong(), any(UserRole.class))).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> adminService.setRole(saveUser.getId(), request));

		verify(userRepository).findByIdAndRoleNot(anyLong(), any(UserRole.class));
	}
}
