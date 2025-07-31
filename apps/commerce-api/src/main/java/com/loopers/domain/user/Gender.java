package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum Gender {
    M,
    F;

    public static Gender from(final String gender) {
        try {
            return Gender.valueOf(gender);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별이 유효하지 않습니다.");
        }
    }
}
