package com.example.sixnumber.user.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Users")
public class User implements UserDetails {

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
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 12)
	private UserRole role;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	@Column(name = "sign")
	private String sign;

	public User(SignupRequest request, String password) {
		this.email = request.getEmail();
		this.password = password;
		this.nickname = request.getNickname();
		this.role = UserRole.ROLE_USER;
		this.status = Status.ACTIVE;
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
	public void setStatus(String status) {
		switch (status) {
			case "ACTIVE" -> this.status = Status.ACTIVE;
			case "SUSPENDED" -> this.status = Status.SUSPENDED;
			case "DORMANT" -> this.status = Status.DORMANT;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();
		for (UserRole eachRole : UserRole.values()) {
			authorities.add(new SimpleGrantedAuthority(eachRole.name()));
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
