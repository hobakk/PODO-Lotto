package com.example.sixnumber.lotto.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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
        this.topNumberList = topNumberList;
        this.bonus = bonus;
    }
}
