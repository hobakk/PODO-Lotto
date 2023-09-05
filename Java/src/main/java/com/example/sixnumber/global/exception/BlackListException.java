// package com.example.sixnumber.global.exception;
//
// import com.example.sixnumber.global.dto.ExceptionDto;
//
// import io.jsonwebtoken.ClaimJwtException;
// import io.jsonwebtoken.Claims;
//
// public class BlackListException extends ClaimJwtException {
// 	private final ExceptionDto exceptionDto;
//
// 	public BlackListException(Claims claims) {
// 		super(null, claims, null);
// 		this.exceptionDto = new ExceptionDto(401, "BLACKED", "허용되지 않는 AccessToken 입니다");
// 	}
//
// 	public ExceptionDto getExceptionDto() { return exceptionDto; }
// }
