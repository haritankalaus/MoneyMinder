package com.jay.accountmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseType expenseType;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Person person;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String category;
    
    private String paymentMethod;
    
    private String status; // PAID, PENDING, OVERDUE
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    private String recurringPeriod; // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    
    @Column(name = "notification_enabled")
    private boolean notificationEnabled = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
