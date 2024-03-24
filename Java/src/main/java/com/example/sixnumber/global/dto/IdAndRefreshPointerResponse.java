package com.example.sixnumber.global.dto;

import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class IdAndRefreshPointerResponse {
    private final Long userId;
    private final String refreshPointer;

    public IdAndRefreshPointerResponse(Claims claims) {
        this.userId = claims.get("id", Long.class);
        this.refreshPointer = claims.get("key", String.class);
    }
}
