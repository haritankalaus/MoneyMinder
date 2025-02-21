package com.jay.accountmanager.service;

import com.jay.accountmanager.model.Expense;
import com.jay.accountmanager.model.ExpenseType;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.repository.ExpenseRepository;
import com.jay.accountmanager.repository.ExpenseTypeRepository;
import com.jay.accountmanager.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final PersonService personService;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<ExpenseType> getAllExpenseTypes() {
        List<ExpenseType> types = expenseTypeRepository.findAll();
        log.info("Found {} expense types", types.size());
        types.forEach(type -> log.info("ExpenseType: {}", type));
        return types;
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));
    }

    @Transactional
    public Expense createExpense(Expense expense) {
        // Set the current user
        Person currentPerson = personService.getCurrentPerson();
        expense.setPerson(currentPerson);
        
        // Set expense type based on the category from the payload
        String category = expense.getCategory().toUpperCase(); // Convert to uppercase for case-insensitive match
        log.info("Looking for expense type with category: {}", category);
        
        List<ExpenseType> allTypes = expenseTypeRepository.findAll();
        log.info("All expense types: {}", allTypes);
        
        ExpenseType expenseType = expenseTypeRepository.findByCategory(category)
                .orElseThrow(() -> new EntityNotFoundException("ExpenseType not found for category: " + category));
        log.info("Found expense type: {}", expenseType);
        
        expense.setExpenseType(expenseType);
        return expenseRepository.save(expense);
    }

    @Transactional
    public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = getExpenseById(id);
        
        // Update fields
        existingExpense.setExpenseType(expense.getExpenseType());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDate(expense.getDate());
        existingExpense.setDescription(expense.getDescription());
        existingExpense.setPaymentMethod(expense.getPaymentMethod());
        existingExpense.setStatus(expense.getStatus());
        existingExpense.setDueDate(expense.getDueDate());
        existingExpense.setRecurringPeriod(expense.getRecurringPeriod());
        existingExpense.setNotificationEnabled(expense.isNotificationEnabled());
        
        return expenseRepository.save(existingExpense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expense expense = getExpenseById(id);
        expenseRepository.delete(expense);
    }

    public List<Expense> getExpensesByType(String category) {
        return expenseRepository.findByExpenseTypeCategory(category);
    }
}
