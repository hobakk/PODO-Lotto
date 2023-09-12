package com.example.sixnumber.global.config;

import java.util.Collections;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Configurations implements WebMvcConfigurer {

	@Bean
	public ServletContextInitializer clearJsession() {
		return servletContext -> {
			servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
			SessionCookieConfig sessionCookieConfig=servletContext.getSessionCookieConfig();
			sessionCookieConfig.setHttpOnly(true);
		};
	}
}
