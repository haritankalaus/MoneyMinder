package com.jay.accountmanager.service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.AccountDTO;
import com.jay.accountmanager.dto.CreditCardDetailsDTO;
import com.jay.accountmanager.dto.LoanDetailsDTO;
import com.jay.accountmanager.model.Account;
import com.jay.accountmanager.model.AccountType;
import com.jay.accountmanager.model.CreditCardDetails;
import com.jay.accountmanager.model.LoanDetails;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public List<AccountDTO> getCurrentUserAccounts() {
        return accountRepository.findByPersonId(personService.getCurrentPerson().getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {SQLException.class, DataAccessException.class})
    public AccountDTO createAccount(AccountDTO accountDTO) {
        if (accountRepository.existsByAccountNumberAndPersonId(accountDTO.getAccountNumber(), personService.getCurrentPerson().getId())) {
            throw new RuntimeException("Account number already exists");
        }

        Person person = personService.getCurrentPerson();

        Account account = Account.builder()
                .name(accountDTO.getName())
                .accountNumber(accountDTO.getAccountNumber())
                .balance(accountDTO.getBalance())
                .type(accountDTO.getType())
                .person(person)
                .build();

        if (accountDTO.getType() == AccountType.CREDIT_CARD && accountDTO.getCreditCardDetails() != null) {
            CreditCardDetails creditCardDetails = mapToCreditCardDetails(accountDTO.getCreditCardDetails(), account);
            account.setCreditCardDetails(creditCardDetails);
        }

        if (accountDTO.getType() == AccountType.LOAN && accountDTO.getLoanDetails() != null) {
            LoanDetails loanDetails = mapToLoanDetails(accountDTO.getLoanDetails(), account);
            account.setLoanDetails(loanDetails);
        }

        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    @Transactional(rollbackFor = {SQLException.class, DataAccessException.class})
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        Account account = getAccountForCurrentUser(id);
        account.setName(accountDTO.getName());
        account.setBalance(accountDTO.getBalance());
        account.setType(accountDTO.getType());

        if (accountDTO.getType() == AccountType.CREDIT_CARD && accountDTO.getCreditCardDetails() != null) {
            CreditCardDetails creditCardDetails = account.getCreditCardDetails();
            if (creditCardDetails == null) {
                creditCardDetails = new CreditCardDetails();
                creditCardDetails.setAccount(account);
            }
            creditCardDetails.setBillGenerateDay(accountDTO.getCreditCardDetails().getBillGenerateDay());
            creditCardDetails.setPaymentDueDay(accountDTO.getCreditCardDetails().getPaymentDueDay());
            creditCardDetails.setInterestRate(accountDTO.getCreditCardDetails().getInterestRate());
            creditCardDetails.setLatePaymentFee(accountDTO.getCreditCardDetails().getLatePaymentFee());
            creditCardDetails.setCreditLimit(accountDTO.getCreditCardDetails().getCreditLimit());
            account.setCreditCardDetails(creditCardDetails);
        } else {
            account.setCreditCardDetails(null);
        }

        if (accountDTO.getType() == AccountType.LOAN && accountDTO.getLoanDetails() != null) {
            LoanDetails loanDetails = account.getLoanDetails();
            if (loanDetails == null) {
                loanDetails = new LoanDetails();
                loanDetails.setAccount(account);
            }
            loanDetails.setLoanType(accountDTO.getLoanDetails().getLoanType());
            loanDetails.setMonthlyPayment(accountDTO.getLoanDetails().getMonthlyPayment());
            loanDetails.setPaymentDueDay(accountDTO.getLoanDetails().getPaymentDueDay());
            loanDetails.setStartDate(accountDTO.getLoanDetails().getStartDate());
            loanDetails.setEndDate(accountDTO.getLoanDetails().getEndDate());
            loanDetails.setInterestRate(accountDTO.getLoanDetails().getInterestRate());
            loanDetails.setTotalLoanAmount(accountDTO.getLoanDetails().getTotalLoanAmount());
            loanDetails.setRemainingAmount(accountDTO.getLoanDetails().getRemainingAmount());
            account.setLoanDetails(loanDetails);
        } else {
            account.setLoanDetails(null);
        }

        Account updatedAccount = accountRepository.save(account);
        return mapToDTO(updatedAccount);
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccount(Long id) {
        Account account = getAccountForCurrentUser(id);
        return mapToDTO(account);
    }

    @Transactional(rollbackFor = {SQLException.class, DataAccessException.class})
    public void deleteAccount(Long id) {
        Account account = getAccountForCurrentUser(id);
        accountRepository.delete(account);
    }

    public Account getAccountForCurrentUser(Long id) {
        Long personId = personService.getCurrentPerson().getId();
        return accountRepository.findById(id)
                .filter(account -> account.getPerson().getId().equals(personId))
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .type(account.getType())
                .creditCardDetails(mapToCreditCardDetailsDTO(account.getCreditCardDetails()))
                .loanDetails(mapToLoanDetailsDTO(account.getLoanDetails()))
                .build();
    }

    private CreditCardDetailsDTO mapToCreditCardDetailsDTO(CreditCardDetails creditCardDetails) {
        if (creditCardDetails == null) {
            return null;
        }
        return CreditCardDetailsDTO.builder()
                .id(creditCardDetails.getId())
                .billGenerateDay(creditCardDetails.getBillGenerateDay())
                .paymentDueDay(creditCardDetails.getPaymentDueDay())
                .interestRate(creditCardDetails.getInterestRate())
                .latePaymentFee(creditCardDetails.getLatePaymentFee())
                .creditLimit(creditCardDetails.getCreditLimit())
                .build();
    }

    private LoanDetailsDTO mapToLoanDetailsDTO(LoanDetails loanDetails) {
        if (loanDetails == null) {
            return null;
        }
        return LoanDetailsDTO.builder()
                .id(loanDetails.getId())
                .loanType(loanDetails.getLoanType())
                .monthlyPayment(loanDetails.getMonthlyPayment())
                .paymentDueDay(loanDetails.getPaymentDueDay())
                .startDate(loanDetails.getStartDate())
                .endDate(loanDetails.getEndDate())
                .interestRate(loanDetails.getInterestRate())
                .totalLoanAmount(loanDetails.getTotalLoanAmount())
                .remainingAmount(loanDetails.getRemainingAmount())
                .build();
    }

    private CreditCardDetails mapToCreditCardDetails(CreditCardDetailsDTO dto, Account account) {
        return CreditCardDetails.builder()
                .account(account)
                .billGenerateDay(dto.getBillGenerateDay())
                .paymentDueDay(dto.getPaymentDueDay())
                .interestRate(dto.getInterestRate())
                .latePaymentFee(dto.getLatePaymentFee())
                .creditLimit(dto.getCreditLimit())
                .build();
    }

    private LoanDetails mapToLoanDetails(LoanDetailsDTO dto, Account account) {
        return LoanDetails.builder()
                .account(account)
                .loanType(dto.getLoanType())
                .monthlyPayment(dto.getMonthlyPayment())
                .paymentDueDay(dto.getPaymentDueDay())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .interestRate(dto.getInterestRate())
                .totalLoanAmount(dto.getTotalLoanAmount())
                .remainingAmount(dto.getRemainingAmount())
                .build();
    }
}
