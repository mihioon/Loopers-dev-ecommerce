package com.loopers.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

// 임의의 포트를 사용하여 테스트 실행
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class E2EIntegrationTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        databaseCleanUp.truncateAllTables();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }
}
