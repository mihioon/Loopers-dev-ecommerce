package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;

public record UserCriteria() {
    public record Register(
            String loginId,
            String name,
            String gender,
            String email,
            String dob
    ) {
        public UserCommand.Register toCommand() {
            return new UserCommand.Register(
                    loginId,
                    name,
                    gender,
                    email,
                    dob
            );
        }
    }
}
