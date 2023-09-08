// package com.example.sixnumber.token;
//
// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletResponse;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.example.sixnumber.global.dto.UnifiedResponse;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/jwt")
// public class TokenController {
// 	private final TokenService tokenService;
//
// 	@PostMapping("/re-issuance")
// 	public ResponseEntity<UnifiedResponse<?>> reIssuance(
// 		@RequestBody ReIssuanceRequest request, HttpServletResponse response
// 	) {
// 		Cookie accessCookie = tokenService.reIssuance(request);
// 		response.addCookie(accessCookie);
// 		return ResponseEntity.ok(UnifiedResponse.ok("AccessToken 재발급 성공"));
// 	}
// }
