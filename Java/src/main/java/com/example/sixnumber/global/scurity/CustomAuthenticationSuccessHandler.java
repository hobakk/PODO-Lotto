package com.example.sixnumber.global.scurity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;
	private final Manager manager;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		User user = manager.findUser(email);

		TokenDto tokenDto = jwtProvider.generateTokens(user);
		redisDao.setRefreshToken(tokenDto.getRefreshPointer(), tokenDto.getRefreshToken(), (long) 7, TimeUnit.DAYS);

		Cookie accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, tokenDto.getAccessToken(), "oneWeek");
		String encodedRefreshToken = encoder.encode(tokenDto.getRefreshToken());
		Cookie JsessionId = jwtProvider.createCookie("JSESSIONID", null, 0);

		response.addCookie(accessCookie);
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + encodedRefreshToken);
		response.addCookie(JsessionId);

		getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/oauth2/user");
	}
}
