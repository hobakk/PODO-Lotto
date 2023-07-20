package com.example.sixnumber.global.exception;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseEntity {
	private int status;
	private String code;
	private String msg;

	public static ResponseEntity<ErrorResponseEntity> comprehensive(ErrorCode e) {
		return ResponseEntity
			.status(e.getHttpStatus())
			.body(ErrorResponseEntity.builder()
				.status(e.getHttpStatus().value())
				.code(e.name())
				.msg(e.getMsg())
				.build()
			);
	}

	public static ResponseEntity<ErrorResponseEntity> individual(BaseException e) {
		String[] sentence = String.valueOf(e).split("\\.");
		String target = sentence[sentence.length -1].split(":")[0];

		return ResponseEntity
			.status(e.getStatus())
			.body(ErrorResponseEntity.builder()
				.status(e.getStatus().value())
				.code(target)
				.msg(e.getMessage())
				.build()
			);
	}
}
