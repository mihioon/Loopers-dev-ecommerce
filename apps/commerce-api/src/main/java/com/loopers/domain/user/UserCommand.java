package com.loopers.domain.user;

public class UserCommand {
    public record Register(
            String loginId,
            String name,
            String gender,
            String email,
            String dob
    ) {
    }
}
