package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    /**
     * - [x]ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.(UserModel)
     * - [x]이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.(UserModel)
     * - [x]생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.(UserModel)
     */
    @DisplayName("ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "test",
            "testTentex",
            "test1234567890",
            "test1234567",
            "1234567890",
            "test____",
            "",
            "1",
            "A"
    })
    void fail_whenIdFormatIsInvalid(String loginId) {
        // given
        final String name = "test";
        final String gender = "F";
        final String email = "test@example.com";
        final String dob = "2025-01-01";

        // when
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity userEntity = UserEntity.create(loginId, name, gender, email, dob);
        });

        // then
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "test",
            "test.example.com",
            "test.example",

            "@example.com",
            "@com",

            "test@",
            "test@.",
            "test@..",

            "test@com",
            "test@com.",
            "test@.com",

            "test @example.com",
            "test@ example.com",

            "test@@example.com",

            ""
    })
    void fail_whenEmailFormatIsInvalid(String email) {
        // given
        final String loginId = "test123456";
        final String gender = "F";
        final String name = "test";
        final String dob = "2025-01-01";

        // when
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity userEntity = UserEntity.create(loginId, name, gender, email, dob);
        });

        // then
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "19900101",
            "1990/01/01",
            "90-01-01",
            "1990-1-1",
            "abcd-ef-gh",
            "1990-01",
            "1990",
            "01-01-1990",
            ""
    })
    void fail_whenDobFormatIsInvalid(String dob) {
        // given
        final String loginId = "test123456";
        final String gender = "F";
        final String name = "test";
        final String email = "test@example.com";

        // when
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity userEntity = UserEntity.create(loginId, name, gender, email, dob);
        });

        // then
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

}
