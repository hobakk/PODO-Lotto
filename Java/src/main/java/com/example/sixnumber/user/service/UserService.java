package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.exception.StatusNotActiveException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.CookieAndTokenResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisDao redisDao;
	private final Manager manager;

	public UnifiedResponse<?> signUp(SignupRequest request, Errors errors) {
		Optional<User> dormantUser = userRepository.findByStatusAndEmail(Status.DORMANT, request.getEmail());
		if (dormantUser.isPresent()) {
			User user = dormantUser.get();
			if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
				user.setStatus(Status.ACTIVE);
				user.setWithdrawExpiration(null);
				userRepository.save(user);
				return UnifiedResponse.ok("재가입 완료");
			}
		}

		List<String> mailList = Arrays.asList("gmail.com", "naver.com", "daum.net");
		String email = request.getEmail();
		if (email.split("@").length != 2 || errors.hasErrors() || mailList.contains(email.split("@")[1])) {
			throw new CustomException(INVALID_INPUT);
		}

		if (userRepository.existsUserByEmail(request.getEmail())) {
			throw new OverlapException("중복된 이메일입니다");
		}
		if (userRepository.existsUserByNickname(request.getNickname())) {
			throw new OverlapException("중복된 닉네임입니다");
		}

		String password = passwordEncoder.encode(request.getPassword());
		User user = new User(request, password);
		user.setStatement(LocalDate.now() + ",회원가입 기념 1000원 증정");
		userRepository.save(user);
		return UnifiedResponse.create("회원가입 완료");
	}

	public CookieAndTokenResponse signIn(SigninRequest request) {
		User user = manager.findUser(request.getEmail());

		if (user.getPassword().equals("Oauth2Login")) throw new CustomException(NOT_OAUTH2_LOGIN);

		if (!user.getStatus().equals(Status.ACTIVE)) {
			String msg;
			switch (user.getStatus()) {
				case SUSPENDED: msg = "정지된 계정입니다"; break;
				case DORMANT: msg = "탈퇴한 계정입니다"; break;
				default: msg = "잘못된 상태정보입니다"; break;
			} throw new StatusNotActiveException(msg);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다");
		}

		CookieAndTokenResponse response;
		String refreshInRedis = redisDao.getValue(user.getRefreshPointer());
		if (refreshInRedis == null) {
			TokenDto tokenDto = jwtProvider.generateTokens(user);
			redisDao.setRefreshToken(tokenDto.getRefreshPointer(), tokenDto.getRefreshToken(), (long) 7, TimeUnit.DAYS);

			Cookie accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, tokenDto.getAccessToken(), "oneWeek");
			String enCodedRefreshToken = passwordEncoder.encode(tokenDto.getRefreshToken());
			response = new CookieAndTokenResponse(accessCookie, "Bearer " + enCodedRefreshToken);
		} else {
			redisDao.deleteValues(user.getRefreshPointer(), JwtProvider.REFRESH_TOKEN);
			throw new OverlapException("중복 로그인 입니다");
		}

		return response;
	}

	public Cookie logout(HttpServletRequest request, User user) {
		String accessToken = jwtProvider.getAccessTokenInCookie(request);
		redisDao.deleteValues(user.getRefreshPointer(), JwtProvider.REFRESH_TOKEN);
		user.setRefreshPointer(null);
		if (accessToken != null) redisDao.setBlackList(accessToken);

		return jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, null, 0);
	}

	public UnifiedResponse<?> withdraw(OnlyMsgRequest request, String email) {
		String withdrawMsg = "회원탈퇴";

		if (!request.getMsg().equals(withdrawMsg)) {
			throw new IllegalArgumentException("잘못된 문자열 입력");
		}

		User user = manager.findUser(email);
		user.setStatus(Status.DORMANT);
		user.setWithdrawExpiration(LocalDate.now().plusMonths(1));
		return UnifiedResponse.ok("회원 탈퇴 완료");
	}

	public UnifiedResponse<?> setPaid(OnlyMsgRequest request, String email) {
		User user = manager.findUser(email);

		if (request.getMsg().equals("월정액 해지")) {
			if (!user.getRole().equals(UserRole.ROLE_PAID)) throw new IllegalArgumentException("월정액 사용자가 아닙니다");

			if (Boolean.TRUE.equals(user.getCancelPaid())) throw new OverlapException("프리미엄 해제 신청을 이미 하셨습니다");

			user.setCancelPaid(true);
			return UnifiedResponse.ok("해지 신청 성공");
		}

		if (user.getCash() < 5000 || user.getRole().equals(UserRole.ROLE_PAID)) {
			throw new IllegalArgumentException("금액이 부족하거나 이미 월정액 이용자입니다");
		}

		user.setCash("-", 5000);
		user.setRole(UserRole.ROLE_PAID);
		user.setPaymentDate(LocalDate.now().plusDays(31));
		user.setStatement(LocalDate.now() + "," + YearMonth.now() + "월 정액 비용 5000원 차감");
		return UnifiedResponse.ok("권한 변경 성공");
	}

	public UnifiedResponse<CashNicknameResponse> getCashNickname(User user) {
		return UnifiedResponse.ok("조회 성공", new CashNicknameResponse(user)) ;
	}

	// 요청을 최대 3번까지 할 수 있고 12시간 기준으로 삭제되기에 충전 요청 취소를 만들지 않아도 된다 판단함
	public UnifiedResponse<?> charging(ChargingRequest chargingRequest, User user) {
		Set<String> keys = redisDao.getKeysList(user.getId());

		if (keys.size() >= 3) throw new IllegalArgumentException("처리되지 않은 요청사항이 많습니다");

		String msgCash = chargingRequest.getMsg() + "-" + chargingRequest.getCash();
		Set<String> checkIncorrect = redisDao.getKeysList(msgCash);
		if (!checkIncorrect.isEmpty()) throw new OverlapException("다른 문자로 다시 시도해주세요");

		if (user.getTimeOutCount() >= 4) throw new CustomException(BREAK_THE_ROLE);

		String value = user.getId() + "-" + chargingRequest.getMsg() + "-" + chargingRequest.getCash();
		redisDao.setValues(value, value, (long) 12, TimeUnit.HOURS);
		user.setTimeOutCount(1);
		userRepository.save(user);
		return UnifiedResponse.ok("요청 성공");
	}

	public UnifiedResponse<List<ChargingResponse>> getCharges(Long userId) {
		List<String> values = redisDao.multiGet(userId);

		List<ChargingResponse> responses = values.stream()
			.map(ChargingResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("신청 리스트 조회 성공", responses);
	}

	public UnifiedResponse<?> update(SignupRequest request, User user) {
		// password 를 프론트로 보내지 않기로 결정함 (보안 문제)
		String password = request.getPassword();
		if (password.equals("")) password = user.getPassword();

		List<String> userIf = Arrays.asList(user.getEmail(), user.getPassword(), user.getNickname());
		List<String> inputData = Arrays.asList(request.getEmail(), password, request.getNickname());

		if (userIf.equals(inputData)) throw new IllegalArgumentException("변경된 부분이 없습니다");

		// 변경할 수 있는 값이 적고, 확실하여 하드 코딩해도 된다 판단함
		for (int i = 0; i < userIf.size(); i++) {
			if (i == 1) {
				if (passwordEncoder.matches(inputData.get(i), userIf.get(i)) ||
					inputData.get(i).equals(userIf.get(i))) continue;
				else inputData.set(i, passwordEncoder.encode(inputData.get(i)));
			}
			if (userIf.get(i).equals(inputData.get(i))) continue;
			if (i == 0) {
				if (userRepository.existsUserByEmail(inputData.get(i))) throw new OverlapException("중복된 이메일입니다");
			} else if (i == 2) {
				if (userRepository.existsUserByNickname(inputData.get(i))) throw new OverlapException("중복된 닉네임입니다");
			}
			userIf.set(i, inputData.get(i));
		}

		user.update(userIf);
		userRepository.save(user);
		return UnifiedResponse.ok("수정 완료");
	}

	public UnifiedResponse<List<StatementResponse>> getStatement(String email) {
		User user = manager.findUser(email);
		if (user.getStatement().size() == 0) throw new IllegalArgumentException("거래내역이 존재하지 않습니다");

		List<StatementResponse> response = user.getStatement()
			.stream()
			.map(str -> {
				String[] localDateMsg = str.split(",");
				return new StatementResponse(localDateMsg);
			})
			.collect(Collectors.toList());
		return UnifiedResponse.ok("거래내역 조회 완료", response);
	}

	public UnifiedResponse<UserResponse> getMyInformation(Long userId) {
		User userIf = manager.findUser(userId);
		UserResponse response = new UserResponse(userIf);
		return UnifiedResponse.ok("조회 성공", response);
	}

	public UnifiedResponse<List<SixNumberResponse>> getBuySixNumberList(Long userId) {
		User userIf = manager.findUser(userId);
		List<SixNumber> sixNumberList = userIf.getSixNumberList();
		if (sixNumberList.size() == 0) throw new CustomException(NO_MATCHING_INFO_FOUND);

		Collections.reverse(sixNumberList);
		if (sixNumberList.size() >= 10) sixNumberList = sixNumberList.subList(0, 10);

		List<SixNumberResponse> response = sixNumberList.stream()
			.map(SixNumberResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("조회 성공", response);
	}

	public UnifiedResponse<?> checkPW(OnlyMsgRequest request, String encodedPassword) {
		if (!passwordEncoder.matches(request.getMsg(), encodedPassword)) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
		}

		return UnifiedResponse.ok("본인확인 성공");
	}

	public UserResponseAndEncodedRefreshDto oauth2LoginAfterGetUserIfAndRefreshToken(Long userIf) {
		User user = manager.findUser(userIf);
		String refreshToken = redisDao.getValue(user.getRefreshPointer());
		if (refreshToken == null) throw new CustomException(INVALID_TOKEN);

		String encodedRefreshToken = passwordEncoder.encode(refreshToken);
		return new UserResponseAndEncodedRefreshDto(new UserResponse(user), encodedRefreshToken);
	}
}
