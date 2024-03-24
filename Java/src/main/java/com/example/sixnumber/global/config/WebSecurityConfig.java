package com.example.sixnumber.global.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.sixnumber.global.scurity.CustomAccessDeniedHandler;
import com.example.sixnumber.global.scurity.CustomAuthenticationSuccessHandler;
import com.example.sixnumber.global.scurity.ExceptionHandlerFilter;
import com.example.sixnumber.global.scurity.JwtEntryPoint;
import com.example.sixnumber.global.scurity.JwtSecurityFilter;
import com.example.sixnumber.global.scurity.UserDetailsServiceImpl;
import com.example.sixnumber.global.util.CustomOAuth2UserService;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;
	private final UserRepository userRepository;
	private final JwtEntryPoint jwtEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private static final String[] URL_PERMIT_ALL = {
		"/api/users/signin", "/api/users/signup", "/api/winnumber",
		"/api/users/email", "/api/users/email/auth-code", "/api/users/find-password",
		"/api/lotto/yearMonth/all", "/api/users/delete-cookie", "/api/users/check-user/issuance-access/**"
	};
	private static final String[] URL_PERMIT_ADMIN = {
		"/api/admin/**", "/api/winnumber/set", "/api/board/admin/**", "/api/lotto/stats/**",
		"/api/winnumber/first"
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtSecurityFilter jwtSecurityFilter() {
		return new JwtSecurityFilter(userDetailsService, jwtProvider, redisDao, userRepository);
	}

	@Bean
	public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler(jwtProvider, redisDao, userRepository);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().cors()

			.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				.antMatchers(URL_PERMIT_ALL).permitAll()
				.antMatchers(URL_PERMIT_ADMIN).hasRole("ADMIN")
				.antMatchers("/api/lotto/**", "/api/users/sixnumber-list").hasAnyRole("ADMIN", "PAID")
				.anyRequest().authenticated()

			.and()
			.exceptionHandling()
				.authenticationEntryPoint(jwtEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)

			.and()
			.addFilterBefore(jwtSecurityFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandlerFilter(), JwtSecurityFilter.class)

			.oauth2Login()
				.successHandler(customAuthenticationSuccessHandler())
				.userInfoEndpoint()
				.userService(customOAuth2UserService);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
}