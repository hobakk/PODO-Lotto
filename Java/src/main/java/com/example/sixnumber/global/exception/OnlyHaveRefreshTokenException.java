package com.example.sixnumber.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class OnlyHaveRefreshTokenException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private final String msg = "RefreshToken 만 확인되어 재갱신을 위한 오류처리";
}
