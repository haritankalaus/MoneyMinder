package com.jay.accountmanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Converter
public class CustomDaysJsonConverter implements AttributeConverter<List<Integer>, String> {
    private static final Logger logger = LoggerFactory.getLogger(CustomDaysJsonConverter.class);
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        CustomDaysJsonConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting List<Integer> to JSON", e);
            throw new RuntimeException("Error converting List<Integer> to JSON", e);
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // Remove surrounding quotes if present
            String jsonData = dbData;
            if (dbData.startsWith("\"") && dbData.endsWith("\"")) {
                jsonData = dbData.substring(1, dbData.length() - 1);
            }
            
            // Handle empty array string
            if (jsonData.equals("[]")) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(jsonData, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to List<Integer>. Input: " + dbData, e);
            // Return empty list instead of throwing exception
            return new ArrayList<>();
        }
    }
}
