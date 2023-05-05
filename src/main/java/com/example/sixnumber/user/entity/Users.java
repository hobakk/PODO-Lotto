package com.example.sixnumber.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "email", nullable = false, unique = true, length = 50)
	private String email;
	@Column(name = "password", nullable = false, length = 60)
	private String password;
	@Column(name = "nickname", nullable = false, unique = true, length = 10)
	private String nickname;
	@Column(name = "cash")
	private int cash;
	@Enumerated
	@Column(name = "role", nullable = false, length = 12)
	private UserRole role;
	private String sign;

	public Users(SignupRequest request, String password) {
		this.email = request.getEmail();
		this.password = password;
		this.nickname = request.getNickname();
		this.role = UserRole.ROLE_USER;
		this.cash = 1000;
	}

	public void setCash(String sign, int cash) {
		switch (sign) {
			case "+" -> this.cash += cash;
			case "-" -> this.cash -= cash;
		}
	}

	public void setAdmin() {
		this.role = UserRole.ROLE_ADMIN;
	}
}
