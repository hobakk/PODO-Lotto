package com.example.sixnumber.global.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.sixnumber.user.entity.User;

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
@RequiredArgsConstructor
public class JwtProvider {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";
	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 임시로 작성해놓았습니다. 의견주시면 감사하겠습니다.
	private static final int expire = 1000;//30분

	public static String accessToken(User user) {
		Date curDate = new Date();
		Date expireDate = new Date(curDate.getTime() + expire);
		HashMap<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		HashMap<String, Object> payloads = new HashMap<>();
		payloads.put("id", user.getId());
		payloads.put("nickname", user.getNickname());
		payloads.put("role", user.getRole());
		return Jwts.builder()
			.setHeader(headers)
			.setSubject(user.getEmail())
			.setClaims(payloads)
			.setIssuedAt(curDate)
			.setExpiration(expireDate)
			.signWith(KEY)
			.compact();
	}

	public static String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(6);
		}
		return null;
	}

	public static boolean validateToken(String token) throws ExpiredJwtException {
		try {
			Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public static Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
