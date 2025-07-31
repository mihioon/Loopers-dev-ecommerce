package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class LoginId {
    final static String PATTERN_USER_ID = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{1,10}$";

    private String loginId;

    public LoginId(final String loginId) {

        if (loginId == null || !loginId.matches(PATTERN_USER_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내로 입력해주세요.");
        }

        this.loginId = loginId;
    }

    public String getLoginId() {
        return loginId;
    }
}
