package com.jay.accountmanager.model;

import java.math.BigDecimal;
import java.util.Date;

import com.jay.security.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Date dateOfBirth;
    
    @Builder.Default
    private String language = "en";

    @Builder.Default
    private String currency = "USD";

    @Builder.Default
    private boolean notifications = true;
    
    @Column(name = "savings_goal", precision = 19, scale = 4)
    private BigDecimal savingsGoal;
    
}
