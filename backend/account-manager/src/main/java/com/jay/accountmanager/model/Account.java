package com.jay.accountmanager.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "accounts")
public class Account {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_seq_gen")
    @SequenceGenerator(name = "accounts_seq_gen",sequenceName = "accounts_seq_gen", allocationSize = 1)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String accountNumber;

    @NotNull
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference
    private Person person;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private CreditCardDetails creditCardDetails;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private LoanDetails loanDetails;
}
