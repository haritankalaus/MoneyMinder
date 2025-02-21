package com.jay.accountmanager.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private YearMonth period;

    @NotNull
    @Column(precision = 19, scale = 4)
    private BigDecimal totalBudget;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalActual;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetCategory> categories;

    @Enumerated(EnumType.STRING)
    private BudgetStatus status;

    private String notes;
}
