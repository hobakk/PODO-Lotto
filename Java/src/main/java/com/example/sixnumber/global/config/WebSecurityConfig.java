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
import com.example.sixnumber.global.scurity.ExceptionHandlerFilter;
import com.example.sixnumber.global.scurity.JwtEntryPoint;
import com.example.sixnumber.global.scurity.JwtSecurityFilter;
import com.example.sixnumber.global.scurity.UserDetailsServiceImpl;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;
	private final JwtEntryPoint jwtEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private static final String[] URL_PERMIT_ALL = {
		"/api/users/signin", "/api/users/signup", "/api/winnumber", "/api/jwt/re-issuance"
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtSecurityFilter jwtSecurityFilter() {
		return new JwtSecurityFilter(userDetailsService, jwtProvider, redisDao);
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
				.antMatchers("/api/admin/**", "/api/winnumber/set").hasRole("ADMIN")
				.antMatchers("/api/lotto/**", "/api/users/sixnumber-list").hasAnyRole("ADMIN", "PAID")
				.anyRequest().authenticated()

			.and()
			.exceptionHandling()
				.authenticationEntryPoint(jwtEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)

			.and()
			.addFilterBefore(jwtSecurityFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandlerFilter(), JwtSecurityFilter.class);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
}