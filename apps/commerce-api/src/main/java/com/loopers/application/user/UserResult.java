package com.loopers.application.user;

import com.loopers.domain.user.UserInfo;

import java.time.LocalDate;

public record UserResult (
        Long id,
        String loginId,
        String name,
        String gender,
        String email,
        LocalDate dob
){
    public static UserResult from(UserInfo userInfo) {
        return new UserResult(
                userInfo.id(),
                userInfo.fetchLoginId(),
                userInfo.name(),
                userInfo.gender().name(),
                userInfo.fetchEmail(),
                userInfo.fetchBirthDate()
        );
    }
}
