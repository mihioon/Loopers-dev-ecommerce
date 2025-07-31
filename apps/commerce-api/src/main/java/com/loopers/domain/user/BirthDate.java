package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
public class BirthDate {

    private LocalDate dob;

    public BirthDate(final String dob) {
        if (dob == null || dob.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 필수값입니다.");
        }

        try {
            this.dob = LocalDate.parse(dob);
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다.");
        }
    }

    public LocalDate getDob() {
        return dob;
    }
}
