package com.example.sixnumber;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;
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
		UserRepository userRepository, PasswordEncoder passwordEncoder, LottoRepository lottoRepository
	) {
		return args -> {

			SignupRequest signupRequest = new SignupRequest("admin","asdf","관리자");
			String password = passwordEncoder.encode(signupRequest.getPassword());
			User admin = new User(signupRequest, password);
			admin.setAdmin();
			admin.setCash("+", 999998999);
			admin.setStatement("DATE,MSG");
			userRepository.save(admin);

			SignupRequest signupRequest1 = new SignupRequest("asdf","asdf","유저1");
			User user1 = new User(signupRequest1, password);
			user1.setCash("+", 20000);
			user1.setStatement("DATE,MSG");
			userRepository.save(user1);

			SignupRequest signupRequest2 = new SignupRequest("asd","asdf","유저2");
			User user2 = new User(signupRequest2, password);
			user2.setCash("+", 20000);
			user2.setStatement("DATE,MSG");
			userRepository.save(user2);

			YearMonth yearMonth = YearMonth.of(2023, 5);
			List<Integer> countList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
			Lotto lotto = new Lotto("test", "dummy", yearMonth, countList, "2 3 4 5 6 7");
			lottoRepository.save(lotto);
		};
	}
}
