package com.loopers.interfaces.api.user;

import com.loopers.support.E2EIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserV1ApiE2ETest extends E2EIntegrationTest {
    /**
     * - [x]회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.
     * - [x]회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.
     *
     * - [x]내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.
     * - [x]존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
     */

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("POST /api/v1/users")
    @Nested
    class SignUp {
        private static final String ENDPOINT = "/api/v1/users";

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInformation_whenSignUpSuccessful() throws Exception {
            // given
            String json = """
                {
                    "loginId": "test123456",
                    "name": "박이름",
                    "gender": "F",
                    "email": "test@example.com",
                    "dob": "2025-01-01"
                }
                """;

            // given

            // when&then
            mockMvc.perform(post(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.loginId").value("test123456"))
                    .andExpect(jsonPath("$.data.name").value("박이름"))
                    .andExpect(jsonPath("$.data.gender").value("F"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.dob").value("2025-01-01"));
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                """
                {
                    "loginId": "test123456",
                    "name": "박이름",
                    "email": "test@example.com",
                    "dob": "2025-01-01"
                }
                """,
                """
                {
                    "loginId": "test123456",
                    "name": "박이름",
                    "gender": null,
                    "email": "test@example.com",
                    "dob": "2025-01-01"
                }
                """,
                """
                {
                    "loginId": "test123456",
                    "name": "박이름",
                    "gender": "",
                    "email": "test@example.com",
                    "dob": "2025-01-01"
                }
                """
        })
        void returnsBadRequest_whenGenderIsMissing(String json) throws Exception {
            /**
             * not provided : 의도적 누락
             * missing : 필수값 누락
             */
            // given

            // when&then
            mockMvc.perform(post(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    @DisplayName("POST /api/v1/users/me")
    @Nested
    class GetMyInfo {
        private static final String ENDPOINT = "/api/v1/users/me";

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInformation_whenGetMyInfoSuccessful() throws Exception {
            // given
            final String loginId = "test123456";
            String json = String.format("""
                {
                    "loginId": "%s",
                    "name": "박이름",
                    "gender": "F",
                    "email": "test@example.com",
                    "dob": "2025-01-01"
                }
                """, loginId);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.loginId").value(loginId));
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returnsNotFound_whenUserDoesNotExist() throws Exception {
            // given
            final String loginId = "test123456";

            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }
    }
}
