package com.jay.accountmanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incomes")
public class Income {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncomeType type;

    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    private Integer recurrenceDay; // Day of month for monthly, or day of week for weekly
    
    private LocalDate nextDueDate;
    
    @Column(nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    private String description;
    
}
