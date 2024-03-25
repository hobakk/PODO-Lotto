package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.user.dto.*;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.lotto.service.WinNumberService;
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
	private final SixNumberRepository sixNumberRepository;
	private final WinNumberService winNumberService;
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
				String authCode = String.valueOf(generateRandomNumber(888888) + 111111);
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

	public UnifiedResponse<?> signIn(HttpServletResponse response, SigninRequest request) {
		User user = userRepository
			.findByEmailAndPasswordNotContainingAndStatus(request.getEmail(), "Oauth2Login", Status.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일 또는 접속할 수 없는 상태입니다"));

		validatePasswordMatching(request.getPassword(), user.getPassword());

		UnifiedResponse<?> unifiedResponse;
		if (user.getRefreshPointer() == null) {
			TokenDto tokenDto = jwtProvider.generateTokens(user);
			user.setRefreshPointer(tokenDto.getRefreshPointer());
			redisDao.setValues(RedisDao.RT_KEY + tokenDto.getRefreshPointer(),
				tokenDto.getRefreshToken(), (long) 7, TimeUnit.DAYS);
			jwtProvider.addCookiesToHeaders(response, tokenDto, JwtProvider.ONE_WEEK);
			unifiedResponse = UnifiedResponse.ok("로그인 성공");
		} else {
			redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
			user.setRefreshPointer(null);
			unifiedResponse = UnifiedResponse.badRequest("중복 로그인입니다");
		}

		return unifiedResponse;
	}

	public UnifiedResponse<?> logout(HttpServletRequest request, User user) {
		redisDao.delete(RedisDao.RT_KEY + user.getRefreshPointer());
		user.setRefreshPointer(null);
		userRepository.save(user);
		jwtProvider.resolveTokens(request).ifPresent((dto) -> {
			if (dto.hasAccessToken()) {
				String accessToken = dto.getAccessToken();
				Long remainingTime = jwtProvider.getRemainingTime(accessToken);
				redisDao.setBlackList(accessToken, remainingTime);
			}
		});
		return UnifiedResponse.ok("로그아웃 성공");
	}

	public UnifiedResponse<?> withdraw(OnlyMsgRequest request, String email) {
		if (!request.getMsg().equals("회원탈퇴")) throw new IllegalArgumentException("잘못된 문자열 입력");

		userRepository.findByEmail(email).ifPresent(User::changeToDORMANT);
		return UnifiedResponse.ok("회원 탈퇴 완료");
	}

	public UnifiedResponse<?> changeToUser(Long userId) {
		return userRepository.findByIdAndRoleAndCancelPaidFalseOrCancelPaidIsNull(userId, UserRole.ROLE_PAID)
			.map(user -> {
				user.setCancelPaid(true);
				return UnifiedResponse.ok("해지 신청 성공");
			})
			.orElseThrow(() -> {
				String msg = "월정액 사용자가 아니거나, 프리미엄 해제 신청을 이미한 계정입니다";
				return new IllegalArgumentException(msg);
			});
	}

	public UnifiedResponse<?> changeToPaid(Long userId) {
		return userRepository.findByIdAndCashGreaterThanEqualAndRoleNot(userId, 5000, UserRole.ROLE_PAID)
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

		Set<String> keyList = redisDao.getKeysList(RedisDao.CHARGE_KEY + key);
		if (!keyList.isEmpty()) throw new OverlapException("다른 문자로 재시도 해주세요");

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

	public UnifiedResponse<?> update(SignupRequest request, User user) {
		String email = user.getEmail().equals(request.getEmail()) ?
				user.getEmail() : userRepository.existsUserByEmail(request.getEmail()) ?
				"" : request.getEmail();

		String password = request.getPassword().isEmpty() ?
				user.getPassword() : passwordEncoder.matches(user.getPassword(), request.getPassword()) ?
				user.getPassword() : passwordEncoder.encode(request.getPassword());

		String nickname = user.getNickname().equals(request.getNickname()) ?
				user.getNickname() : userRepository.existsUserByNickname(request.getNickname()) ?
				"" : request.getNickname();

		if (email.isEmpty() || nickname.isEmpty()) throw new OverlapException("이메일 또는 닉네임 중복입니다");

		user.update(email, password, nickname);
		userRepository.save(user);
		return UnifiedResponse.ok("수정 완료");
	}

	public UnifiedResponse<List<StatementResponse>> getStatement(Long userId) {
		return userRepository.findByIdAndStatementListNotNull(userId)
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

	public UnifiedResponse<List<SixNumberResponse>> getSixNumberList(Long userId) {
		return userRepository.findByIdAndSixNumberListNotNull(userId)
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

	public UnifiedResponse<?> attendance(User user) {
		return redisDao.getValue(RedisDao.ATTENDANCE_KEY + user.getId())
			.map(value -> UnifiedResponse.badRequest("오늘 이미 출석하셨습니다"))
			.orElseGet(() -> {
				int randomNumber = generateRandomNumber(100);
				int point = reward(randomNumber);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				Date nextDayMidnight = calendar.getTime();

				long millisecondsUntilMidnight = nextDayMidnight.getTime() - System.currentTimeMillis();
				redisDao.setValues(RedisDao.ATTENDANCE_KEY + user.getId(),
					String.valueOf(point), millisecondsUntilMidnight, TimeUnit.MILLISECONDS);

				user.plusCash(point);
				userRepository.save(user);
				return UnifiedResponse.ok(point + " 포인트 당첨!!");
			});
	}

	public UnifiedResponse<List<WinningNumberResponse>> checkLottoWinLastWeek(Long userId) {
		WinNumberResponse lastWeekWinNumber = winNumberService.getFirstWinNumber();
		String[] splitYearMonthString = lastWeekWinNumber.getDate().split("-");
		int year = Integer.parseInt(splitYearMonthString[0]);
		int month = Integer.parseInt(splitYearMonthString[1]);
		int day = Integer.parseInt(splitYearMonthString[2]);
		
		LocalDateTime winningDate = LocalDateTime.of(year, month, day, 22, 0);
		LocalDateTime startDate = winningDate.minusDays(6).minusHours(14);
		List<WinningNumberResponse> winningNumberResponses =
			sixNumberRepository.findAllByUserIdAndBuyDateAfterAndBuyDateBefore(userId, startDate, winningDate).stream()
				.findAny()
				.map(sixNumber -> sixNumber.getNumberList().stream()
					.filter(sentence ->  getWinningNumbers(lastWeekWinNumber, sentence) >= 3)
					.map(sentence -> {
						int numberOfWins = getWinningNumbers(lastWeekWinNumber, sentence);
						if (numberOfWins == 5) {
							String replaceValue = sentence
								.replaceFirst(String.valueOf(lastWeekWinNumber.getBonus()), "");
							if (sentence.length() != replaceValue.length()) numberOfWins = 7;
						}

						return new WinningNumberResponse(sentence, numberOfWins);
					})
					.collect(Collectors.toList()))
				.orElseThrow(()-> new CustomException(NOT_FOUND));

		if (winningNumberResponses.isEmpty()) throw new IllegalArgumentException("아쉽지만 당첨되지 않으셨습니다");

		return UnifiedResponse.ok("당첨 이력 조회 성공", winningNumberResponses);
	}

    public UnifiedResponse<?> checkUserIdNextIssuanceNewAccessToken(
            HttpServletRequest request,
            HttpServletResponse response,
            Long userIdInRedux
    ) {
        return jwtProvider.resolveTokens(request)
                .filter(TokenDto::onlyHaveRefreshToken)
                .map(dto -> {
                    Claims claims = jwtProvider.getClaims(dto.getRefreshToken());
                    Long userIdInRefreshToken = claims.get("id", Long.class);
                    UnifiedResponse<?> unifiedResponse;
                    if (Objects.equals(userIdInRefreshToken, userIdInRedux)) {
                        String refreshPointer = claims.get("key", String.class);
                        String newAccessToken = jwtProvider.accessToken(refreshPointer);
                        TokenDto tokenDto = new TokenDto(newAccessToken);
                        jwtProvider.addCookiesToHeaders(response, tokenDto, "oneWeek");
                        unifiedResponse = UnifiedResponse.ok("accessToken 재발급 성공");
                    } else unifiedResponse = UnifiedResponse.badRequest("accessToken 재발급 실패");

                    return unifiedResponse;
                })
                .orElseThrow(() -> new CustomException(INVALID_ACCESS));
    }

	public UnifiedResponse<?> deleteCookie(HttpServletResponse response) {
		jwtProvider.addCookiesToHeaders(response, new TokenDto(), 0);
		return UnifiedResponse.ok("쿠키 삭제 성공");
	}

	private int generateRandomNumber(int range) {
		Random random = new Random();
		return random.nextInt(range);
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

	private int reward(int randomNumber) {
		int point;
		if (randomNumber <= 30) point = 10;
		else if (randomNumber <= 60) point = 30;
		else if (randomNumber <= 90) point = 50;
		else point = 100;

		return point;
	}

	private int getWinningNumbers(WinNumberResponse lastWeekWinNumber, String sentence) {
		int count = 0;
		for (String numberStr : sentence.split(" ")) {
			for (int winningNumber : lastWeekWinNumber.getTopNumberList()) {
				if (Integer.parseInt(numberStr) == winningNumber) {
					count++;
					break;
				}
			}
		}

		return count;
	}
}
