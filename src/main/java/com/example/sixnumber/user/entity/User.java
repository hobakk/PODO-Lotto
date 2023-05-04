package com.example.sixnumber.user.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.type.UserRole;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickname;
	private int cash;
	private UserRole role;
	private String sign;

	public User(SignupRequest request, String password) {
		this.email = request.getEmail();
		this.password = password;
		this.nickname = request.getNickname();
		this.role = UserRole.ROLE_USER;
		this.cash = 1000;
	}

	public void setCash(String sign, int cash) {
		switch (sign) {
			case "+":
				this.cash += cash;
				break;
			case "-":
				this.cash -= cash;
				break;
		}
	}

	public void setAdmin() {
		this.role = UserRole.ROLE_ADMIN;
	}

}
