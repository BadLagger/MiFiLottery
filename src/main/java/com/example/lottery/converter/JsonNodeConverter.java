package com.example.lottery.converter;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        try {
            return attribute.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readTree(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
