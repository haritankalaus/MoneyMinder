package com.jay.accountmanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.accountmanager.dto.MonthAndDayDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class MonthAndDayJsonConverter implements AttributeConverter<MonthAndDayDTO, String> {
    private static final Logger logger = LoggerFactory.getLogger(MonthAndDayJsonConverter.class);
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        MonthAndDayJsonConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(MonthAndDayDTO attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting MonthAndDayDTO to JSON", e);
            throw new RuntimeException("Error converting MonthAndDayDTO to JSON", e);
        }
    }

    @Override
    public MonthAndDayDTO convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            // Remove surrounding quotes if present
            String jsonData = dbData;
            if (dbData.startsWith("\"") && dbData.endsWith("\"")) {
                jsonData = dbData.substring(1, dbData.length() - 1);
            }

            // Handle empty object
            if (jsonData.equals("{}")) {
                return null;
            }

            return objectMapper.readValue(jsonData, MonthAndDayDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to MonthAndDayDTO. Input: " + dbData, e);
            return null;
        }
    }
}
