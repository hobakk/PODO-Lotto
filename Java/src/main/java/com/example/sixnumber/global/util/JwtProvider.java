package com.example.sixnumber.global.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;

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
	private final RedisTemplate<String, String> redisTemplate;

	public JwtProvider(RedisTemplate<String, String> redisTemplate, @Value("${spring.jwt.secret-key}") String keyValue) {
		String keyBase64Encoded = Base64.getEncoder().encodeToString(keyValue.getBytes());
		byte[] decodedKey = Base64.getDecoder().decode(keyBase64Encoded);
		this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		this.redisTemplate = redisTemplate;
	}

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";
	private static final Duration expire = Duration.ofMinutes(30);
	private static final Duration refreshExpire = Duration.ofDays(7);


	public String accessToken(String email, Long userId) {
		Instant now = Instant.now();
		Instant expiration = now.plus(expire);
		return setToken(email, userId, now, expiration);
	}

	public String refreshToken(String email, Long userId) {
		Instant now = Instant.now();
		Instant expiration = now.plus(refreshExpire);
		return setToken(email, userId, now, expiration);
	}

	public String setToken(String email, Long userId, Instant now, Instant expiration) {
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(email)
			.claim("id", userId)
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiration))
			.signWith(secretKey)
			.compact();
	}

	public String resolveToken(HttpServletRequest request, String tokenType) {
		String token = switch (tokenType) {
			case "access" -> AUTHORIZATION_HEADER;
			case "refresh" -> "Refresh-Token";
			default -> throw new IllegalArgumentException("잘못된 TokenType 입니다");
		};

		String bearerToken = request.getHeader(token);
		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String getIdEmail(String token) {
		Long userId = getClaims(token).get("id", Long.class);
		String email = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();

		return userId + "," + email;
	}

	public Long getTokenInUserId(String token) {
		return getClaims(token).get("id", Long.class);
	}

	public Boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
		return true;
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public String[] validateRefreshToken(String refreshToken) {
		String[] idEmail = getIdEmail(refreshToken).split(",");
		String refreshTokenInRedis = redisTemplate.opsForValue().get("RT: " + idEmail[0]);

		if (Objects.isNull(refreshTokenInRedis)
			|| !refreshToken.equals(refreshTokenInRedis) || isTokenExpired(refreshTokenInRedis)) {
			redisTemplate.delete("RT: " + idEmail[0]);
			return null;
		}

		return idEmail;
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public void setExpire(String token) {
		getClaims(token).setExpiration(new Date());
	}

	public boolean isTokenExpired(String token) {
		Date expirationDate = getClaims(token).getExpiration();
		if	(expirationDate == null || expirationDate.before(new Date())) return true;
		return false;
	}
}
