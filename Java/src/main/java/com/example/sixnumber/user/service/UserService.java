package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

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
import com.example.sixnumber.user.dto.EmailAuthCodeRequest;
import com.example.sixnumber.user.dto.EmailRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisDao redisDao;
	private final Manager manager;

	public UnifiedResponse<?> sendAuthCodeToEmail(EmailRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

		List<String> mailList = Arrays.asList("gmail.com", "naver.com", "daum.net");
		if (!mailList.contains(request.getEmail().split("@")[1])) throw new CustomException(INVALID_INPUT);

		String toEmail = request.getEmail();
		if (userRepository.existsUserByEmail(toEmail)) throw new OverlapException("중복된 이메일입니다");

		Random random = new Random();
		String authCode = String.valueOf(random.nextInt(888888) + 111111);
		redisDao.setValues(RedisDao.AUTH_KEY + toEmail, authCode, 30L, TimeUnit.MINUTES);
		manager.sendEmail(toEmail, authCode);
		return UnifiedResponse.ok("인증번호 발급 성공");
	}

	public UnifiedResponse<?> compareAuthCode(EmailAuthCodeRequest request) {
		String authCode = redisDao.getValue(RedisDao.AUTH_KEY + request.getEmail());
		if (!request.getAuthCode().equals(authCode)) throw new IllegalArgumentException("인증번호가 일치하지 않습니다");

		return UnifiedResponse.ok("인증번호 일치");
	}

	public UnifiedResponse<?> signUp(SignupRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

		Optional<User> dormantUser = userRepository.findByStatusAndEmail(Status.DORMANT, request.getEmail());
		if (dormantUser.isPresent()) {
			User user = dormantUser.get();
			validatePasswordMatching(request.getPassword(), user.getPassword());
			user.setStatus(Status.ACTIVE);
			user.setWithdrawExpiration(null);
			userRepository.save(user);
			return UnifiedResponse.ok("재가입 완료");
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

	public UnifiedResponse<?> signIn(HttpServletResponse response, SigninRequest request) {
		User user = manager.findUser(request.getEmail());
		if (user.getPassword().equals("Oauth2Login")) throw new CustomException(NOT_OAUTH2_LOGIN);

		List<Status> notActive = Arrays.asList(Status.SUSPENDED, Status.DORMANT);
		if (notActive.contains(user.getStatus())) throw new StatusNotActiveException();

		validatePasswordMatching(request.getPassword(), user.getPassword());

		UnifiedResponse<?> unifiedResponse;
		if (user.getRefreshPointer() == null) {
			TokenDto tokenDto = jwtProvider.generateTokens(user);
			user.setRefreshPointer(tokenDto.getRefreshPointer());
			redisDao.setValues(RedisDao.RT_KEY + tokenDto.getRefreshPointer(),
				tokenDto.getRefreshToken(), (long) 7, TimeUnit.DAYS);

			Cookie accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN,
				tokenDto.getAccessToken(), JwtProvider.ONE_WEEK);

			String enCodedRefreshToken = passwordEncoder.encode(tokenDto.getRefreshToken());
			response.addCookie(accessCookie);
			response.addHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + enCodedRefreshToken);
			unifiedResponse = UnifiedResponse.ok("로그인 성공");
		} else {
			redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
			user.setRefreshPointer(null);
			unifiedResponse = UnifiedResponse.badRequest("중복 로그인입니다");
		}

		return unifiedResponse;
	}

	public Cookie logout(HttpServletRequest request, User user) {
		String accessToken = jwtProvider.getAccessTokenInCookie(request);
		redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
		user.setRefreshPointer(null);
		userRepository.save(user);
		if (accessToken != null) {
			Long remainingTime = jwtProvider.getRemainingTime(accessToken);
			redisDao.setBlackList(accessToken, remainingTime);
		}

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

	public UnifiedResponse<?> changeToUser(User user) {
		if (user.getRole().equals(UserRole.ROLE_USER)) throw new IllegalArgumentException("월정액 사용자가 아닙니다");
		if (Boolean.TRUE.equals(user.getCancelPaid())) throw new OverlapException("프리미엄 해제 신청을 이미 하셨습니다");

		user.setCancelPaid(true);
		userRepository.save(user);
		return UnifiedResponse.ok("해지 신청 성공");
	}

	public UnifiedResponse<?> changeToPaid(String email) {
		User user = manager.findUser(email);
		if (user.getCash() < 5000 || user.getRole().equals(UserRole.ROLE_PAID)) {
			throw new IllegalArgumentException("금액이 부족하거나 이미 월정액 이용자입니다");
		}

		user.minusCash(5000);
		user.setRole(UserRole.ROLE_PAID);
		user.setPaymentDate(LocalDate.now().plusDays(31));
		user.setStatement(LocalDate.now() + "," + YearMonth.now() + "월 정액 비용 5000원 차감");
		return UnifiedResponse.ok("권한 변경 성공");
	}

	public UnifiedResponse<CashNicknameResponse> getCashAndNickname(User user) {
		return UnifiedResponse.ok("조회 성공", new CashNicknameResponse(user)) ;
	}

	public UnifiedResponse<?> charging(ChargingRequest chargingRequest, User user) {
		if (user.getTimeOutCount() >= 4) throw new CustomException(BREAK_THE_ROLE);

		String charge = redisDao.getValue(RedisDao.CHARGE_KEY + user.getId());
		if (charge != null) throw new CustomException(INVALID_INPUT);

		String chargeInfo = String.format("%d-%s-%d-%s",
			user.getId(), chargingRequest.getMsg(), chargingRequest.getCash(),
			dateFormatter(LocalDateTime.now().plusHours(1)));

		redisDao.setValues(RedisDao.CHARGE_KEY + user.getId(), chargeInfo, (long) 1, TimeUnit.HOURS);
		user.setTimeOutCount(1);
		userRepository.save(user);
		return UnifiedResponse.ok("요청 성공");
	}

	public UnifiedResponse<ChargingResponse> getCharge(Long userId) {
		String charge = redisDao.getValue(RedisDao.CHARGE_KEY + userId);
		if (charge == null) throw new CustomException(NOT_FOUND);

		ChargingResponse responses = new ChargingResponse(charge);
		return UnifiedResponse.ok("신청 리스트 조회 성공", responses);
	}

	// 무슨 경우에서도 프론트로 password 를 보내지 않음
	public UnifiedResponse<?> update(SignupRequest request, User user) {
		String password = request.getPassword();
		if (password.equals("")) password = user.getPassword();

		List<String> userIf = Arrays.asList(user.getEmail(), user.getPassword(), user.getNickname());
		List<String> inputData = Arrays.asList(request.getEmail(), password, request.getNickname());
		if (userIf.equals(inputData)) throw new IllegalArgumentException("변경된 부분이 없습니다");

		for (int i = 0; i < userIf.size(); i++) {
			switch (i) {
				case 0: if (userRepository.existsUserByEmail(inputData.get(i)))
					throw new OverlapException("중복된 이메일입니다"); break;
				case 1: if (passwordEncoder.matches(inputData.get(i), userIf.get(i))) break;
					else inputData.set(i, passwordEncoder.encode(inputData.get(i))); continue;
				case 2: if (userRepository.existsUserByNickname(inputData.get(i)))
					throw new OverlapException("중복된 닉네임입니다"); break;
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

		List<StatementResponse> response = user.getStatement().stream()
			.map(StatementResponse::new)
			.collect(Collectors.toList());
		return UnifiedResponse.ok("거래내역 조회 완료", response);
	}

	public UnifiedResponse<List<SixNumberResponse>> getBuySixNumberList(Long userId) {
		User user = manager.findUser(userId);
		List<SixNumber> sixNumberList = user.getSixNumberList();
		if (sixNumberList.size() == 0) throw new CustomException(NO_MATCHING_INFO_FOUND);

		Collections.reverse(sixNumberList);
		if (sixNumberList.size() >= 12) sixNumberList = sixNumberList.subList(0, 12);

		List<SixNumberResponse> response = sixNumberList.stream()
			.map(res -> new SixNumberResponse(dateFormatter(res.getBuyDate()), res.getNumberList()))
			.collect(Collectors.toList());
		return UnifiedResponse.ok("조회 성공", response);
	}

	public UnifiedResponse<?> comparePassword(OnlyMsgRequest request, String encodedPassword) {
		validatePasswordMatching(request.getMsg(), encodedPassword);
		return UnifiedResponse.ok("본인확인 성공");
	}

	public UnifiedResponse<UserResponse> getMyInformation(Long userId) {
		User userIf = manager.findUser(userId);
		UserResponse response = new UserResponse(userIf);
		return UnifiedResponse.ok("조회 성공", response);
	}

	public UserResponseAndEncodedRefreshDto oauth2LoginAfterGetUserIfAndRefreshToken(Long userIf) {
		User user = manager.findUser(userIf);
		String refreshToken = redisDao.getValue(RedisDao.RT_KEY + user.getRefreshPointer());
		if (refreshToken == null) throw new CustomException(INVALID_TOKEN);

		String encodedRefreshToken = passwordEncoder.encode(refreshToken);
		return new UserResponseAndEncodedRefreshDto(new UserResponse(user), encodedRefreshToken);
	}

	private String dateFormatter(LocalDateTime localDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
		return localDateTime.format(formatter);
	}

	private void validatePasswordMatching(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword))
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
	}

	private void errorsHandler(Errors errors) {
		List<FieldError> fieldErrors = errors.getFieldErrors();
		List<String> errorMsgList = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			errorMsgList.add(fieldError.getDefaultMessage());
		}

		String errorMsg = IntStream.range(0, errorMsgList.size())
			.mapToObj(index -> (index + 1) + ". " + errorMsgList.get(index))
			.collect(Collectors.joining(".\n"));
		throw new OverlapException(errorMsg);
	}
}
