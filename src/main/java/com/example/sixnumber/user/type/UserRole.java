package com.example.sixnumber.user.type;

public enum UserRole {
	ROLE_USER(Authority.USER),
	ROLE_ADMIN(Authority.ADMIN);

	private final String authority;

	UserRole(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public static class Authority {
		public static final String USER = "ROLE_USER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}
