package com.example.sixnumber.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{ ErrorCode errorCode; }

