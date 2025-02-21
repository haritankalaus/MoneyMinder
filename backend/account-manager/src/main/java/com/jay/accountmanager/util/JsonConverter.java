package com.jay.accountmanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.accountmanager.dto.MonthAndDayDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonConverter {
    private final ObjectMapper objectMapper;

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public MonthAndDayDTO fromJsonMonthAndDay(String json) {
        try {
            return objectMapper.readValue(json, MonthAndDayDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to MonthAndDay", e);
        }
    }

    public List<Integer> fromJsonCustomDays(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to custom days list", e);
        }
    }
}
