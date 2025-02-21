package com.jay.accountmanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.jay.accountmanager.model.BillType;
import com.jay.accountmanager.model.RecurrenceType;

import lombok.Data;

@Data
public class BillDTO {
    private Long id;
    private String name;
    private BigDecimal amount;
    private BillType type;
    private String description;
    private LocalDate dueDate;
    private RecurrenceType recurrenceType;
    private Integer dayOfWeek;    // 1-7 for WEEKLY bills
    private Integer dayOfMonth;   // 1-31 for MONTHLY bills
    private MonthAndDayDTO monthAndDay;   // For YEARLY bills
    private List<Integer> customDays;    // Array of days for CUSTOM recurrence
    private LocalDate recurrenceEnd;
    private boolean isAutoPay;
    private String paymentMethod;
    private String category;
    private Long accountId;
}
