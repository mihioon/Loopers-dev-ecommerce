package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
            "1234567890",
            ""
    })
    void fail_whenIdFormatIsInvalid(String userId) {
        // arrange

        // act
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity exampleModel = UserEntity.create(
                    userId,
                    "test",
                    "test@example.com",
                    "2025-01-01"
            );
        });

        // assert
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
        // arrange

        // act
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity exampleModel = UserEntity.create(
                    "test123456",
                    "test",
                    email,
                    "2025-01-01"
            );
        });

        // assert
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
        // arrange

        // act
        final CoreException exception = assertThrows(CoreException.class, () -> {
            UserEntity exampleModel = UserEntity.create(
                    "test123456",
                    "test",
                    "test@example.com",
                    dob
            );
        });

        // assert
    }

}
