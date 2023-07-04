package com.example.sixnumber;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sixnumber.test.entity.TestUser;
import com.example.sixnumber.test.repository.TestUserRepository;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner dummyData(
		UserRepository userRepository, PasswordEncoder passwordEncoder, TestUserRepository testUserRepository
	) {
		return args -> {

			SignupRequest signupRequest = new SignupRequest("admin","asdf","관리자");
			String password = passwordEncoder.encode(signupRequest.getPassword());
			User admin = new User(signupRequest, password);
			admin.setAdmin();
			admin.setCash("+", 999998999);
			admin.setStatement("테스트");
			userRepository.save(admin);
		};
	}
}
