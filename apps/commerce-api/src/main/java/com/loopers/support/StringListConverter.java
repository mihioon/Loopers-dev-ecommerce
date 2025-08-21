package com.loopers.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<Long>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    // 엔티티 → DB
    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        try {
            return (attribute == null ? "[]" : mapper.writeValueAsString(attribute));
        } catch (Exception e) {
            throw new RuntimeException("리스트 → JSON 변환 실패", e);
        }
    }

    // DB → 엔티티
    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return (dbData == null || dbData.isBlank())
                    ? new ArrayList<>()
                    : mapper.readValue(dbData, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON → 리스트 변환 실패", e);
        }
    }
}
