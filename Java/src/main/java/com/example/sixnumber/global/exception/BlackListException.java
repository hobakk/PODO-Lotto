package com.example.sixnumber.global.exception;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;

public class BlackListException extends ClaimJwtException {

	public BlackListException(Header header, Claims claims, String message) {
		super(header, claims, message);
	}
}
