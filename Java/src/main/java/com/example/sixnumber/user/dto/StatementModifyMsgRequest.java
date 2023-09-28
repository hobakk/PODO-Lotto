package com.example.sixnumber.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementModifyMsgRequest {
	private Long statementId;
	private String msg;
}
