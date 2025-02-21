package com.jay.accountmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jay.accountmanager.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    
    @Query("SELECT p FROM Person p WHERE p.user.id = :userId")
    Optional<Person> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Person p WHERE p.user.username = :username")
    Optional<Person> findByUsername(@Param("username") String username);
}
