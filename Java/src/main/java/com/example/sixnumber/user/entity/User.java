package com.example.sixnumber.user.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@Column(name = "paymentDate")
	private LocalDate paymentDate;
	@Column(name = "cancelPaid")
	private Boolean cancelPaid;
	@Column(name = "withdrawExpiration")
	private LocalDate withdrawExpiration;
	@ElementCollection
	@OrderColumn(name = "statement_index", nullable = false)
	private List<String> statement;
	@Column(name = "timeOutCount")
	private int timeOutCount;
	@Column(name = "refreshPointer")
	private String refreshPointer;
	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SixNumber> sixNumberList;

	public User(SignupRequest request, String password) {
		this.email = request.getEmail();
		this.password = password;
		this.nickname = request.getNickname();
		this.role = UserRole.ROLE_USER;
		this.status = Status.ACTIVE;
		this.cash = 1000;
		this.statement = new ArrayList<>();
		this.timeOutCount = 0;
		this.refreshPointer = null;
		this.sixNumberList = new ArrayList<>();
	}

	// OAuth2
	public User(String email, String nickname, UserRole role) {
		this.email = email;
		this.password = "Oauth2Login";
		this.nickname = nickname;
		this.role = role;
		this.status = Status.ACTIVE;
		this.cash = 1000;
		this.statement = new ArrayList<>();
		this.timeOutCount = 0;
		this.refreshPointer = null;
		this.sixNumberList = new ArrayList<>();
	}

	// Controller Test
	public User(String email, String password, UserRole role, Status status) {
		this.email = email;
		this.password = password;
		this.nickname = "testUSer";
		this.role = role;
		this.status = status;
		this.cash = 1000;
		this.statement = new ArrayList<>();
		this.timeOutCount = 0;
	}

	public void update(List<String> list) {
		this.email = list.get(0);
		this.password = list.get(1);
		this.nickname = list.get(2);
	}

	public User setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public void setPaymentDate(LocalDate localDate) {
		this.paymentDate = localDate;
	}

	public void setCancelPaid(Boolean type) { this.cancelPaid = type; }

	public void plusCash(int cash) { this.cash += cash; }

	public void minusCash(int cash) { this.cash -= cash; }

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void setAdmin() {
		this.role = UserRole.ROLE_ADMIN;
	}

	public void setStatus(Status status) {
		if (Arrays.asList(Status.values()).contains(status)) this.status = status;
	}

	public void setWithdrawExpiration(LocalDate localDate) {
		this.withdrawExpiration = localDate;
	}

	public void setStatement(String str) {
		this.statement.add(str);
	}

	public void setTimeOutCount(int num) {
		if (num == 0) this.timeOutCount = 0;
		else this.timeOutCount += num;
	}

	public void minusTimeOutCount() { if (this.getTimeOutCount() > 0) this.timeOutCount -= 1; }

	public void setRefreshPointer(String refreshPointer) {
		this.refreshPointer = refreshPointer;
	}

	// test code
	public void setId(Long userId) {
		this.id = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;

		User user = (User) o;
		return id.equals(user.id) && email.equals(user.email);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + email.hashCode();
		return result;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String authority = this.role.getAuthority();
		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		Collection<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(simpleGrantedAuthority);
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
