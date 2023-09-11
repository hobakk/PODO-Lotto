package com.example.sixnumber.global.scurity;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		Long userId = oAuth2User.getAttribute("userId");

		String refreshPointer = UUID.randomUUID().toString();
		String accessToken = jwtProvider.accessToken(refreshPointer);
		String refreshToken = jwtProvider.refreshToken(email, userId, refreshPointer);
		redisDao.setRefreshToken(refreshPointer, refreshToken, (long) 7, TimeUnit.DAYS);

		Cookie accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, accessToken, "oneWeek");
		String encodedRefreshToken = encoder.encode(refreshToken);

		response.addCookie(accessCookie);
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + encodedRefreshToken);

		getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/");
	}
}
