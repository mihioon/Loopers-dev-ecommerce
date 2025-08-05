package com.loopers.interfaces.api.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.catalog.brand.Brand;
import com.loopers.domain.catalog.brand.BrandRepository;
import com.loopers.support.E2EIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BrandV1ApiE2ETest extends E2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BrandRepository brandRepository;

    @DisplayName("POST /api/v1/brands")
    @Nested
    class Create {
    }

    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    class GetBrand {
        private static final String ENDPOINT = "/api/v1/brands/{brandId}";

        @DisplayName("브랜드 조회에 성공할 경우, 해당하는 브랜드 정보를 응답으로 반환한다.")
        @Test
        @Transactional
        void returnsBrandInformation_whenGetBrandSuccessful() throws Exception {
            // given
            Brand brand = brandRepository.save(new Brand("brand", "description"));

            // when&then
            mockMvc.perform(get(ENDPOINT, brand.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.name").value(brand.getName()));
        }

        @DisplayName("존재하지 않는 ID로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returnsNotFound_whenBrandDoesNotExist() throws Exception {
            // given
            final Long brandId = 1L;

            // when&then
            mockMvc.perform(get(ENDPOINT, brandId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.meta.result").value("FAIL"));
        }
    }
}
