package com.jay.accountmanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jay.accountmanager.dto.MonthAndDayDTO;
import com.jay.accountmanager.util.CustomDaysJsonConverter;
import com.jay.accountmanager.util.MonthAndDayJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillType type;

    private String description;

    @Column
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceType recurrenceType;

    private Integer dayOfWeek;    // 1-7 for WEEKLY bills

    private Integer dayOfMonth;   // 1-31 for MONTHLY bills

    @Column(name = "month_and_day", columnDefinition = "TEXT")
    @Convert(converter = MonthAndDayJsonConverter.class)
    private MonthAndDayDTO monthAndDay;   // For YEARLY bills

    @Column(name = "custom_days", columnDefinition = "TEXT")
    @Convert(converter = CustomDaysJsonConverter.class)
    private List<Integer> customDays;    // Array of days for CUSTOM recurrence

    private LocalDate recurrenceEnd;

    @Column(nullable = false)
    private boolean isAutoPay;

    private String paymentMethod;

    private String category;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
