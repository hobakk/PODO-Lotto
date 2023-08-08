package com.example.sixnumber.global.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";
	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static final int expire = 1000 * 60 * 30;
	private static final Long refreshExpire = 7 * 24 * 60 * 1000L;
	private static final Date curDate = new Date();
	private final RedisTemplate<String, String> redisTemplate;

	public String accessToken(String email, Long userId) {
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(email)
			.claim("id", userId)
			.setIssuedAt(curDate)
			.setExpiration(setExpireDate((long) expire))
			.signWith(KEY)
			.compact();
	}

	public String refreshToken(String email, Long userId) {
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(email)
			.claim("id", userId)
			.setIssuedAt(curDate)
			.setExpiration(setExpireDate(refreshExpire))
			.signWith(KEY)
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
			.setSigningKey(KEY)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();

		return userId + "," + email;
	}

	public Boolean validateToken(String token) throws ExpiredJwtException {
		try {
			Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
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

	public Boolean validateRefreshToken(String accessToken, String refreshToken) {
		Long id = getClaims(accessToken).get("id", Long.class);
		String refreshTokenInRedis = redisTemplate.opsForValue().get("RT: " + id);

		if (
			Objects.isNull(refreshTokenInRedis)
			|| isTokenExpired(refreshTokenInRedis)
			|| !refreshToken.equals(refreshTokenInRedis)
		) {
			redisTemplate.delete("RT: " + id);
			return false;
		}

		return true;
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public void setExpire(String token) {
		getClaims(token).setExpiration(new Date());
	}

	public boolean isTokenExpired(String token) {
		Date expirationDate = getClaims(token).getExpiration();
		return expirationDate.after(new Date());
	}

	public Date setExpireDate(Long data) {
		return new Date(curDate.getTime() + data);
	}
}
