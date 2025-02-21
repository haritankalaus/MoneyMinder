package com.jay.accountmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan( basePackages = {"com.jay"} )
public class AccountManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountManagerApplication.class, args);
    }
}
