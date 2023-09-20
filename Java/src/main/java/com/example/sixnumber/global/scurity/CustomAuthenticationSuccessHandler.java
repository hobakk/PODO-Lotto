package com.example.sixnumber.global.scurity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;
	private final UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");

		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));
		String refreshTokenInRedis = redisDao.getValue(RedisDao.RT_KEY + user.getRefreshPointer());
		Cookie accessCookie;
		if (refreshTokenInRedis != null) {
			long remainingSeconds = Math.max(jwtProvider.getRemainingTime(refreshTokenInRedis) /1000, 0);
			String accessToken = jwtProvider.accessToken(user.getRefreshPointer());
			accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, accessToken, remainingSeconds);
		} else {
			TokenDto tokenDto = jwtProvider.generateTokens(user);
			user.setRefreshPointer(tokenDto.getRefreshPointer());
			redisDao.setValues(RedisDao.RT_KEY + tokenDto.getRefreshPointer(), tokenDto.getRefreshToken(), (long) 7, TimeUnit.DAYS);
			accessCookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, tokenDto.getAccessToken(), "oneWeek");
		}

		userRepository.save(user);
		Cookie JsessionId = jwtProvider.createCookie("JSESSIONID", null, 0);
		response.addCookie(accessCookie);
		response.addCookie(JsessionId);
		getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/oauth2/user");
	}
}
