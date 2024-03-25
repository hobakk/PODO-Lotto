package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class UpdateProfileResponse {
    private final String email;
    private final String encodedPassword;
    private final String nickname;

    public UpdateProfileResponse(SignupRequest request) {
        this.email = request.getEmail();
        this.encodedPassword = request.getPassword();
        this.nickname = request.getNickname();
    }

    public UpdateProfileResponse(SignupRequest request, String encodedPassword) {
        this.email = request.getEmail();
        this.encodedPassword = encodedPassword;
        this.nickname = request.getNickname();
    }
}
