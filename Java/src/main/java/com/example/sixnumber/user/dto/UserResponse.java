package com.example.sixnumber.user.dto;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.Getter;

@Getter
public class UserResponse {
	private final Long userId;
	private final String email;
	private final String nickname;
	private final int cash;
	private final UserRole role;
	private final Status status;

	public UserResponse(User user) {
		this.userId = user.getId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.cash = user.getCash();
		this.role = user.getRole();
		this.status = user.getStatus();
	}
}
