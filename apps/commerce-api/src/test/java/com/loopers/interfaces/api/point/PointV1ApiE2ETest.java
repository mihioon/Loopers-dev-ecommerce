package com.loopers.interfaces.api.point;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class PointV1ApiE2ETest {
    /**
     * - [x]포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.
     * - [x]`X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.
     *
     * - [x]존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.
     * - [x]존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.
     */

    @DisplayName("POST /api/v1/points")
    @Nested
    class GetPoint {
        @Autowired
        private MockMvc mockMvc;

        private static final String ENDPOINT = "/api/v1/points";

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Transactional
        @Test
        void returnsPoint_whenGetPointSuccessful() throws Exception {
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
                    .andExpect(status().isOk());
        }

        @DisplayName("`X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Transactional
        @Test
        void returnsBadRequest_whenUserIdIsMissing() throws Exception {
            // when&then
            mockMvc.perform(get(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class ChargePoint {
        @Autowired
        private MockMvc mockMvc;

        private static final String ENDPOINT = "/api/v1/points/charge";

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Transactional
        @Test
        void returnsPoint_whenChargePointSuccessful() throws Exception {
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
            String point = "1000";
            String pointJson = String.format("""
                {
                    "point": %s
                }
                """, point);
            int N = 10;
            for(int i = 1; i <= N; i++) {
                mockMvc.perform(post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-USER-ID", loginId)
                                .content(pointJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                        .andExpect(jsonPath("$.data.point").value((i) * 1000));
            }
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Transactional
        @Test
        void returnsNotFound_whenUserDoesNotExist() throws Exception {
            // given
            final String loginId = "test123456";

            // when&then
            mockMvc.perform(post(ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", loginId)
                            .content("{\"point\": 1000}"))
                    .andExpect(status().isNotFound());
        }
    }
}
