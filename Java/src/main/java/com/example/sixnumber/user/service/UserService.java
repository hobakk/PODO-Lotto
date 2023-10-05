package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import com.example.sixnumber.user.dto.FindPasswordRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementModifyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.entity.Statement;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.StatementRepository;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final StatementRepository statementRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisDao redisDao;
	private final Manager manager;

	public UnifiedResponse<?> sendAuthCodeToEmail(EmailRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

		return Stream.of("gmail.com", "naver.com", "daum.net")
			.filter(email -> email.equals(request.getEmail().split("@")[1]))
			.findFirst()
			.map(email -> {
				Random random = new Random();
				String authCode = String.valueOf(random.nextInt(888888) + 111111);
				redisDao.setValues(RedisDao.AUTH_KEY + request.getEmail(), authCode, 30L, TimeUnit.MINUTES);
				manager.sendEmail(request.getEmail(), authCode);
				return UnifiedResponse.ok("인증번호 발급 성공");
			})
			.orElseThrow(() -> new CustomException(INVALID_INPUT));
	}

	public UnifiedResponse<?> compareAuthCode(EmailAuthCodeRequest request) {
		return redisDao.getValue(RedisDao.AUTH_KEY + request.getEmail())
			.map(value -> {
				if (!request.getAuthCode().equals(value))
					throw new CustomException(NO_MATCHING_INFO_FOUND);

				return UnifiedResponse.ok("인증번호 일치");
			})
			.orElseThrow(() -> new IllegalArgumentException("이메일 인증 이후 요청해주세요"));
	}

	public UnifiedResponse<?> signUp(SignupRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

		return userRepository.findByStatusAndEmail(Status.DORMANT, request.getEmail())
			.map(user -> {
				validatePasswordMatching(request.getPassword(), user.getPassword());
				user.setStatus(Status.ACTIVE);
				user.setWithdrawExpiration(null);
				userRepository.save(user);
				return UnifiedResponse.ok("재가입 완료");
			})
			.orElseGet(() -> {
				if (userRepository.existsUserByEmail(request.getEmail()))
					throw new OverlapException("중복된 이메일입니다");
				if (userRepository.existsUserByNickname(request.getNickname()))
					throw new OverlapException("중복된 닉네임입니다");

				String encodedPassword = passwordEncoder.encode(request.getPassword());
				User user = new User(request, encodedPassword);
				user.addStatement(new Statement(user, "회원가입", 1000));
				userRepository.save(user);
				return UnifiedResponse.create("회원가입 완료");
			});
	}

	public UnifiedResponse<?> signIn(HttpServletResponse response, SigninRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

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
		redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
		user.setRefreshPointer(null);
		userRepository.save(user);

		String accessToken = jwtProvider.getAccessTokenInCookie(request);
		if (accessToken != null) {
			Long remainingTime = jwtProvider.getRemainingTime(accessToken);
			if (remainingTime != 0) redisDao.setBlackList(accessToken, remainingTime);
		}

		return jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, null, 0);
	}

	public UnifiedResponse<?> withdraw(OnlyMsgRequest request, String email) {
		if (!request.getMsg().equals("회원탈퇴")) throw new IllegalArgumentException("잘못된 문자열 입력");

		User user = manager.findUser(email);
		user.changeToDORMANT();
		return UnifiedResponse.ok("회원 탈퇴 완료");
	}

	public UnifiedResponse<?> changeToUser(Long userId) {
		return userRepository.findById(userId)
			.filter(user -> user.getRole() != UserRole.ROLE_USER && !user.getCancelPaid())
			.map(user -> {
				user.setCancelPaid(true);
				return UnifiedResponse.ok("해지 신청 성공");
			})
			.orElseThrow(() -> {
				String msg = "월정액 사용자가 아니거나, 프리미엄 해제 신청을 이미한 계정입니다";
				throw new IllegalArgumentException(msg);
			});
	}

	public UnifiedResponse<?> changeToPaid(Long userId) {
		return userRepository.findById(userId)
			.filter(user -> user.getCash() >= 5000 && user.getRole() != UserRole.ROLE_PAID)
			.map(user -> {
				user.changeToROLE_PAID();
				return UnifiedResponse.ok("권한 변경 성공");
			})
			.orElseThrow(() -> new IllegalArgumentException("금액이 부족하거나 이미 월정액 이용자입니다"));
	}

	public UnifiedResponse<CashNicknameResponse> getCashAndNickname(User user) {
		return UnifiedResponse.ok("조회 성공", new CashNicknameResponse(user)) ;
	}

	public UnifiedResponse<?> charging(ChargingRequest chargingRequest, User user) {
		if (user.getTimeoutCount() >= 4) throw new CustomException(BREAK_THE_ROLE);

		String key = String.format("%d-%s-%d",
			user.getId(), chargingRequest.getMsg(), chargingRequest.getCash());
		Set<String> chargeList = redisDao.getKeysList(RedisDao.CHARGE_KEY + key);
		if (chargeList.size() != 0) throw new OverlapException("다른 문자로 재시도 해주세요");

		String chargeInfo = key + "-" + dateFormatter(LocalDateTime.now().plusHours(1));
		redisDao.setValues(RedisDao.CHARGE_KEY + key, chargeInfo, (long) 1, TimeUnit.HOURS);
		user.setTimeoutCount(1);
		userRepository.save(user);
		return UnifiedResponse.ok("요청 성공");
	}

	public UnifiedResponse<ChargingResponse> getCharge(Long userId) {
		return redisDao.multiGet(RedisDao.CHARGE_KEY + userId).stream()
			.findFirst()
			.map(charge -> {
				ChargingResponse response = new ChargingResponse(charge);
				return UnifiedResponse.ok("충전 요청 조회 성공", response);
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
	}

	public UnifiedResponse<?> deleteCharge(String key, User user) {
		String finalKey = String.format("%d-%s", user.getId(), key);
		redisDao.delete(RedisDao.CHARGE_KEY + finalKey);
		user.minusTimeOutCount();
		userRepository.save(user);
		return UnifiedResponse.ok("충전 요청 삭제 성공");
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

	// Statement 정보 보유기간 및 반환값에 대해 더 고민해야함
	public UnifiedResponse<List<StatementResponse>> getStatement(Long userId) {
		return userRepository.findById(userId)
			.filter(user -> user.getStatementList().size() > 0)
			.map(user -> {
				List<StatementResponse> response = user.getStatementList().stream()
					.filter(res -> res.getLocalDate().isAfter(LocalDate.now().minusMonths(1)))
					.map(StatementResponse::new)
					.collect(Collectors.toList());

				return UnifiedResponse.ok("거래내역 조회 완료", response);
			})
			.orElseThrow(() -> new IllegalArgumentException("거래내역이 존재하지 않습니다"));
	}

	public UnifiedResponse<?> modifyStatementMsg(StatementModifyMsgRequest request) {
		LocalDate lastMonth =  LocalDate.now().minusMonths(1);
		return statementRepository.findByIdAndAfterLastMonth(request.getStatementId(), lastMonth)
			.map(statement -> {
				statement.modifyMsg(request.getMsg());
				return UnifiedResponse.ok("텍스트 수정 성공");
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
	}

	public UnifiedResponse<List<SixNumberResponse>> getBuySixNumberList(Long userId) {
		return userRepository.findById(userId)
			.filter(user -> user.getSixNumberList().size() > 0)
			.map(user -> {
				List<SixNumber> sixNumberList = user.getSixNumberList();
				if (sixNumberList.size() >= 12)
					sixNumberList = sixNumberList.subList(sixNumberList.size() - 12, sixNumberList.size());

				List<SixNumberResponse> response = sixNumberList.stream()
					.map(sixNumber -> new SixNumberResponse(
						dateFormatter(sixNumber.getBuyDate()), sixNumber.getNumberList()
					))
					.collect(Collectors.toList());

				return UnifiedResponse.ok("조회 성공", response);
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
	}

	public UnifiedResponse<?> comparePassword(OnlyMsgRequest request, String encodedPassword) {
		validatePasswordMatching(request.getMsg(), encodedPassword);
		return UnifiedResponse.ok("본인확인 성공");
	}

	public UnifiedResponse<UserResponse> getMyInformation(User user) {
		return UnifiedResponse.ok("조회 성공", new UserResponse(user));
	}

	public UserResponseAndEncodedRefreshDto oauth2LoginAfterGetUserIfAndRefreshToken(Long userId) {
		User user = manager.findUser(userId);
		return redisDao.getValue(RedisDao.RT_KEY + user.getRefreshPointer())
			.map(value -> {
				String encodedRefreshToken = passwordEncoder.encode(value);
				return new UserResponseAndEncodedRefreshDto(new UserResponse(user), encodedRefreshToken);
			})
			.orElseThrow(() -> new CustomException(INVALID_TOKEN));
	}

	public UnifiedResponse<?> findPassword(FindPasswordRequest request, Errors errors) {
		if (errors.hasErrors()) errorsHandler(errors);

		return userRepository.findByEmail(request.getEmail())
			.map(user -> {
				String encodedPassword = passwordEncoder.encode(request.getPassword());
				user.setPassword(encodedPassword);
				return UnifiedResponse.ok("비밀번호 설정 성공");
			})
			.orElseThrow(() -> new CustomException(NOT_FOUND));
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
		List<String> errorMsgList = errors.getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.toList());

		String errorMsg = IntStream.range(0, errorMsgList.size())
			.mapToObj(index -> (index + 1) + ". " + errorMsgList.get(index))
			.collect(Collectors.joining(".\n"));
		throw new OverlapException(errorMsg);
	}
}
