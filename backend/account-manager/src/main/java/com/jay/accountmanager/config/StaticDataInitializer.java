package com.jay.accountmanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jay.accountmanager.model.ExpenseType;
import com.jay.accountmanager.repository.ExpenseTypeRepository;

@Configuration
public class StaticDataInitializer {

    @Bean
    public CommandLineRunner initStaticData(ExpenseTypeRepository expenseTypeRepository) {
        return args -> {
            // Only initialize if roles table is empty
            if (expenseTypeRepository.count() == 0) {
            	ExpenseType expenseTypeA = new ExpenseType();
            	expenseTypeA.setCategory("ACCOUNT");
            	expenseTypeA.setDescription("ACCOUNT");
            	expenseTypeA.setName("ACCOUNT");
            	expenseTypeA.setActive(true);
            	expenseTypeRepository.save(expenseTypeA);

            	ExpenseType expenseType = new ExpenseType();
            	expenseType.setCategory("BILL");
            	expenseType.setDescription("BILL");
            	expenseType.setName("BILL");
            	expenseType.setActive(true);
            	expenseTypeRepository.save(expenseType);

            }
        };
    }
}

