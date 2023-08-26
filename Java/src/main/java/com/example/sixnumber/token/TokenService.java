// package com.example.sixnumber.token;
//
// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletRequest;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.sixnumber.global.exception.CustomException;
// import com.example.sixnumber.global.exception.ErrorCode;
// import com.example.sixnumber.global.util.JwtProvider;
// import com.example.sixnumber.global.util.Manager;
// import com.example.sixnumber.user.dto.CookiesResponse;
// import com.example.sixnumber.user.dto.MyInformationResponse;
// import com.example.sixnumber.user.entity.User;
//
// import io.jsonwebtoken.ExpiredJwtException;
// import lombok.AllArgsConstructor;
//
// @Service
// @Transactional
// @AllArgsConstructor
// public class TokenService {
// 	private final JwtProvider jwtProvider;
// 	private final Manager manager;
//
// 	public UserIfAndCookieResponse getInformationAfterCheckLogin(HttpServletRequest request) {
// 		CookiesResponse cookies = jwtProvider.getTokenValueInCookie(request);
// 		if (cookies.getRefreshCookie() == null) throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
//
// 		String accessToken = cookies.getAccessCookie().getValue();
// 		String refreshToken = cookies.getRefreshCookie().getValue();
//
// 		if (jwtProvider.validateToken(accessToken)) {
// 			User user = manager.findUser(jwtProvider.getTokenInUserId(accessToken));
// 			MyInformationResponse myInformationResponse = new MyInformationResponse(user);
// 			return new UserIfAndCookieResponse(myInformationResponse);
// 		} else {
// 			try {
// 				Cookie cookie = createCookie(refreshToken);
// 				User user = manager.findUser(jwtProvider.getTokenInUserId(refreshToken));
// 				MyInformationResponse myInformationResponse = new MyInformationResponse(user);
// 				return new UserIfAndCookieResponse(myInformationResponse, cookie);
// 			} catch (ExpiredJwtException e) {
// 				throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
// 			}
// 		}
// 	}
//
// 	public Cookie renewAccessToken(String refreshToken) {
// 		return createCookie(refreshToken);
// 	}
//
// 	private Cookie createCookie(String refreshToken) {
// 		if (jwtProvider.isTokenExpired(refreshToken)) throw new CustomException(ErrorCode.EXPIRED_TOKEN);
//
// 		String pointer = jwtProvider.getClaims(refreshToken).getSubject();
// 		String accessToken = jwtProvider.accessToken(pointer);
// 		return jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, accessToken);
// 	}
// }
