package com.example.sixnumber.lotto.dto;

import com.example.sixnumber.lotto.entity.WinNumber;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WinNumberResponse {
    private final int time;
    private final String date;
    private final List<Integer> topNumberList;
    private final int bonus;

    @JsonCreator
    public static WinNumberResponse create(
            @JsonProperty("time") int time,
            @JsonProperty("date") String date,
            @JsonProperty("topNumberList") List<Integer> topNumberList,
            @JsonProperty("bonus") int bonus
    ) {
        return new WinNumberResponse(time, date, topNumberList, bonus);
    }

    public WinNumberResponse(int time, String date, List<Integer> topNumberList, int bonus) {
        this.time = time;
        this.date = date;
        this.topNumberList = new ArrayList<>(topNumberList);
        this.bonus = bonus;
    }

    // TestDataFactory 테스트 용
    public WinNumberResponse(WinNumber winNumber) {
        this.time = winNumber.getTime();
        this.date = winNumber.getDate();
        this.topNumberList = new ArrayList<>(winNumber.getTopNumberList());
        this.bonus = winNumber.getBonus();
    }

    public WinNumberResponse(FullFieldOfWinNumber winNumber) {
        this.time = winNumber.getTime();
        this.date = winNumber.getDate();
        this.topNumberList = new ArrayList<>(winNumber.getTopNumberList());
        this.bonus = winNumber.getBonus();
    }
}
