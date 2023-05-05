package com.example.sixnumber.lotto.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;

@Getter
public class SchedulerResponse <T> {
	// 스케쥴러 타겟 일자 예) 3월 최대 개표 리스트
	private LocalDate targetDate;
	private List<List<T>> numList;

	public SchedulerResponse(LocalDate targetDate, List<List<T>> numList) {
		this.targetDate = targetDate;
		this.numList = numList;
	}
}
