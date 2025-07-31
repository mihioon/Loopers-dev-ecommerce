package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "user")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Embedded
    private LoginId loginId;

    @Embedded
    private Email email;

    @Embedded
    private BirthDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String name;

    public User(
            final LoginId loginId,
            final Email email,
            final BirthDate dob,
            final Gender gender,
            final String name
    ) {
        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 필수입니다.");
        }

        this.loginId = loginId;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.name = name;
    }

    public LoginId getLoginId() {
        return loginId;
    }

    public Email getEmail() {
        return email;
    }

    public BirthDate getDob() {
        return dob;
    }

    public Gender getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }
}
