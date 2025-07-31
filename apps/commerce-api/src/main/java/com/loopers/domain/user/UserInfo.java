package com.loopers.domain.user;

import java.time.LocalDate;

public record UserInfo(
        Long id,
        LoginId loginId,
        String name,
        Gender gender,
        Email email,
        BirthDate dob
) {
    public static UserInfo from(final User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getGender(),
                user.getEmail(),
                user.getDob()
        );
    }

    public String fetchLoginId() {
        return loginId.getLoginId();
    }

    public String fetchEmail() {
        return email.getEmail();
    }

    public LocalDate fetchBirthDate() {
        return dob.getDob();
    }
}
