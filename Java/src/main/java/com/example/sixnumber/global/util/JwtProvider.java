package com.example.sixnumber.global.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.sixnumber.user.dto.CookiesResponse;

import io.jsonwebtoken.Claims;
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

	// public static final String AUTHORIZATION_HEADER = "Authorization";
	// public static final String BEARER_PREFIX = "Bearer";
	public static final String REFRESH_TOKEN = "refreshToken";
	public static final String ACCESS_TOKEN = "accessToken";
	private static final Duration expire = Duration.ofMinutes(5);
	private static final Duration refreshExpire = Duration.ofDays(7);

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
		Date expirationDate = getClaims(token).getExpiration();
		Instant now = Instant.now();
		long remainingMillis = expirationDate.toInstant().toEpochMilli() - now.toEpochMilli();
		return Math.max(remainingMillis, 0);
	}

	public boolean isTokenExpired(String token) {
		Date expirationDate = getClaims(token).getExpiration();
		return expirationDate == null || expirationDate.before(new Date());
	}

	public Cookie createCookie(String key, String tokenValue, int maxAge) {
		Cookie cookie = new Cookie(key, tokenValue);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	public CookiesResponse getTokenValueInCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length != 2) return new CookiesResponse();

		Cookie access = Arrays.stream(cookies).filter(
			cookie -> cookie.getName().equals(JwtProvider.ACCESS_TOKEN)).findFirst().orElse(null);
		Cookie refresh = Arrays.stream(cookies).filter(
			cookie -> cookie.getName().equals(JwtProvider.REFRESH_TOKEN)).findFirst().orElse(null);
		return new CookiesResponse(access, refresh);
	}

	// public void setExpire(String token) {
	// 	getClaims(token).setExpiration(new Date());
	// }

	// public String[] validateRefreshToken(String refreshToken) {
	// 	String[] idEmail = getIdEmail(refreshToken).split(",");
	// 	String refreshTokenInRedis = redisTemplate.opsForValue().get("RT: " + idEmail[0]);
	//
	// 	if (Objects.isNull(refreshTokenInRedis)
	// 		|| !refreshToken.equals(refreshTokenInRedis) || isTokenExpired(refreshTokenInRedis)) {
	// 		redisTemplate.delete("RT: " + idEmail[0]);
	// 		return null;
	// 	}
	//
	// 	return idEmail;
	// }

	// public String getIdEmail(String token) {
	// 	Long userId = getClaims(token).get("id", Long.class);
	// 	String email = Jwts.parserBuilder()
	// 		.setSigningKey(secretKey)
	// 		.build()
	// 		.parseClaimsJws(token)
	// 		.getBody()
	// 		.getSubject();
	//
	// 	return userId + "," + email;
	// }

	// public String resolveToken(HttpServletRequest request) {
	// 	String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
	// 	if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
	// 		return bearerToken.substring(7);
	// 	}
	// 	return null;
	// }
}
