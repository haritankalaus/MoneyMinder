package com.jay.accountmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthAndDayDTO {
    private int month; // 1-12
    private int day;   // 1-31

    @JsonCreator
    public MonthAndDayDTO(
        @JsonProperty("month") int month,
        @JsonProperty("day") int day
    ) {
        this.month = month;
        this.day = day;
    }
}
