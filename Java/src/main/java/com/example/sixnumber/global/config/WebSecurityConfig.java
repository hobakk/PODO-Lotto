package com.example.sixnumber.global.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.sixnumber.global.scurity.JwtSecurityFilter;
import com.example.sixnumber.global.scurity.UserDetailsServiceImpl;
import com.example.sixnumber.global.util.JwtProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
				.antMatchers("/api/users/signin", "/api/users/signup", "/api/winnumber", "/api/users/my-information").permitAll()
				.antMatchers("/api/users/**").hasAnyRole("USER", "ADMIN", "PAID")
				.antMatchers("/api/admin/**", "/api/winnumber/set").hasRole("ADMIN")
				.antMatchers("/api/lotto/**", "/api/lotto/yearMonth/all").hasAnyRole("ADMIN", "PAID")
				.antMatchers("/**").authenticated()
			.and()
			.formLogin()
				.loginPage("/signin")
				.permitAll();

		http.addFilterBefore(new JwtSecurityFilter(userDetailsService, jwtProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}