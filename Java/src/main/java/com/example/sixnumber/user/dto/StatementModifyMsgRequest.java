package com.example.sixnumber.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatementModifyMsgRequest {
	private Long statementId;
	private String msg;
}
