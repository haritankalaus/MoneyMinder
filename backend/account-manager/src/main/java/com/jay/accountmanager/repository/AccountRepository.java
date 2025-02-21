package com.jay.accountmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Account;
import com.jay.accountmanager.model.Person;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByPersonId(Long personId);
    
    List<Account> findByPerson(Person person);

    boolean existsByAccountNumberAndPersonId(String accountNumber, Long personId);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.person = :person")
    BigDecimal getTotalBalance(@Param("person") Person person);
    
    @Query("SELECT a FROM Account a WHERE a.person = :person AND a.type = 'SAVINGS'")
    List<Account> findSavingsAccounts(@Param("person") Person person);
    
    @Query("SELECT a FROM Account a WHERE a.person = :person AND a.type = 'CHECKING'")
    List<Account> findCheckingAccounts(@Param("person") Person person);
}
