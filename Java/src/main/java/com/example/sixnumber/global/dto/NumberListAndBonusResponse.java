package com.example.sixnumber.global.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NumberListAndBonusResponse {
    private final List<Integer> numberList;
    private final int bonus;

    public NumberListAndBonusResponse(String numberSentence) {
        List<Integer> bonusInclude = Arrays.stream(numberSentence.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        this.numberList = bonusInclude.subList(0, bonusInclude.size()-1);
        this.bonus = bonusInclude.subList(bonusInclude.size()-1, bonusInclude.size()).get(0);
    }
}
