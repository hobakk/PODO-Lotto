package com.example.sixnumber.global.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {
	private final SecretKey secretKey;

	public JwtProvider(@Value("${spring.jwt.secret-key}") String keyValue) {
		String keyBase64Encoded = Base64.getEncoder().encodeToString(keyValue.getBytes());
		byte[] decodedKey = Base64.getDecoder().decode(keyBase64Encoded);
		this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
	}

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String ONE_WEEK = "oneWeek";

	private final Duration expire = Duration.ofMinutes(5);
	private final Duration refreshExpire = Duration.ofDays(7);

	// String refreshPointer = UUID.toString();
	public String accessToken(String refreshPointer) {
		Instant now = Instant.now();
		Instant expiration = now.plus(expire);
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(refreshPointer)
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiration))
			.signWith(secretKey)
			.compact();
	}

	public String refreshToken(String email, Long userId, String refreshPointer) {
		Instant now = Instant.now();
		Instant expiration = now.plus(refreshExpire);
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(email)
			.claim("id", userId)
			.claim("key", refreshPointer) // accessToken 재발급시 필요
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiration))
			.signWith(secretKey)
			.compact();
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public Long getTokenInUserId(String token) {
		return getClaims(token).get("id", Long.class);
	}

	public Boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public Long getRemainingTime(String token) {
		try {
			Date expirationDate = getClaims(token).getExpiration();
			Instant now = Instant.now();
			long remainingMillis = expirationDate.toInstant().toEpochMilli() - now.toEpochMilli();
			return Math.max(remainingMillis, 0);
		} catch (ExpiredJwtException e) {
			return (long) 0;
		}
	}

	public void createCookie(HttpServletResponse response, String key, String tokenValue, Object maxAge) {
		int age;
		if (maxAge instanceof Integer) age = ((int) maxAge);
		else if (maxAge.equals(ONE_WEEK)) age = 603000;
		else throw new CustomException(ErrorCode.INVALID_INPUT);

		if (tokenValue == null) tokenValue = "";
		ResponseCookie cookie = ResponseCookie.from(key, tokenValue)
			.path("/")
			.sameSite("None")
			.httpOnly(true)
			.secure(true)
			.maxAge(age)
			.build();

		response.addHeader("Set-Cookie", cookie.toString());

	}

	public String getAccessTokenInCookie(HttpServletRequest request) {
		Cookie accessValueIsNull = new Cookie(JwtProvider.ACCESS_TOKEN, null);

		try {
			Cookie[] cookies = request.getCookies();

			Cookie access = Arrays.stream(cookies).filter(
				cookie -> cookie.getName().equals(JwtProvider.ACCESS_TOKEN)).findFirst().orElse(accessValueIsNull);
			return access.getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public TokenDto generateTokens(User user) {
		String refreshPointer = UUID.randomUUID().toString();
		String accessToken = accessToken(refreshPointer);
		String refreshToken = refreshToken(user.getEmail(), user.getId(), refreshPointer);
		return new TokenDto(accessToken, refreshToken, refreshPointer);
	}
}
