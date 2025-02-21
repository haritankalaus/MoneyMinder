package com.jay.accountmanager.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.TransactionDTO;
import com.jay.accountmanager.model.Account;
import com.jay.accountmanager.model.Category;
import com.jay.accountmanager.model.Transaction;
import com.jay.accountmanager.model.TransactionType;
import com.jay.accountmanager.repository.AccountRepository;
import com.jay.accountmanager.repository.CategoryRepository;
import com.jay.accountmanager.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    public List<TransactionDTO> getAccountTransactions(Long accountId) {
        //Account account = accountService.getAccountForCurrentUser(accountId);
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Account account = accountService.getAccountForCurrentUser(transactionDTO.getAccountId());
        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        validateTransaction(transactionDTO, account);

        Transaction transaction = Transaction.builder()
                .description(transactionDTO.getDescription())
                .amount(transactionDTO.getAmount())
                .date(LocalDateTime.now())
                .type(transactionDTO.getType())
                .account(account)
                .category(category)
                .notes(transactionDTO.getNotes())
                .build();

        updateAccountBalance(account, transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return mapToDTO(savedTransaction);
    }

    private void validateTransaction(TransactionDTO transactionDTO, Account account) {
        if (transactionDTO.getType() == TransactionType.EXPENSE) {
            if (account.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
                throw new RuntimeException("Insufficient funds");
            }
        }
    }

    private void updateAccountBalance(Account account, Transaction transaction) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance = switch (transaction.getType()) {
            case INCOME -> currentBalance.add(transaction.getAmount());
            case EXPENSE -> currentBalance.subtract(transaction.getAmount());
            case TRANSFER -> currentBalance; // Handle transfer separately
        };
        account.setBalance(newBalance);
    }

    public List<TransactionDTO> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
       // Account account = accountService.getAccountForCurrentUser(accountId);
        return transactionRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .notes(transaction.getNotes())
                .build();
    }
}
