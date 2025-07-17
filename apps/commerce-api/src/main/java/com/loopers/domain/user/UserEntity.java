package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {
    final static String PATTERN_USER_ID = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{1,10}$";
    final static String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    final static String PATTERN_DOB = "^\\d{4}-\\d{2}-\\d{2}$";

    private String loginId;
    private String name;
    private String gender;
    private String email;
    private String dob;
    private Long point;

    // constructor
    private UserEntity(String loginId, String name, String gender, String email, String dob, long point) {
        this.loginId = loginId;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.dob = dob;
        this.point = point;
    }

    // static factory method
    public static UserEntity create(
            String loginId,
            String name,
            String gender,
            String email,
            String dob,
            long point
    ) {
        if (loginId == null || !loginId.matches(PATTERN_USER_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내로 입력해주세요.");
        }

        email = email.trim();
        if (!email.matches(PATTERN_EMAIL) || email.contains(" ")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        }

        if (!dob.matches(PATTERN_DOB)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다.");
        }

        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별을 입력해주세요.");
        }

        return new UserEntity(loginId, name, gender, email, dob, point);
    }
}
