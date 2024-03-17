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
    private final String data;
    private final List<Integer> topNumberList;
    private final int bonus;

    @JsonCreator
    public static WinNumberResponse create(
            @JsonProperty("time") int time,
            @JsonProperty("date") String data,
            @JsonProperty("topNumberList") List<Integer> topNumberList,
            @JsonProperty("bonus") int bonus
    ) {
        return new WinNumberResponse(time, data, topNumberList, bonus);
    }

    public WinNumberResponse(int time, String data, List<Integer> topNumberList, int bonus) {
        this.time = time;
        this.data = data;
        this.topNumberList = new ArrayList<>(topNumberList);
        this.bonus = bonus;
    }

    // TestDataFactory 테스트 용
    public WinNumberResponse(WinNumber winNumber) {
        this.time = winNumber.getTime();
        this.data = winNumber.getData();
        this.topNumberList = winNumber.getTopNumberList();
        this.bonus = winNumber.getBonus();
    }
}
