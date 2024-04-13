package com.example.synchronizestudy.service;

import com.example.synchronizestudy.domain.BankAccount;
import com.example.synchronizestudy.repository.BankAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BankAccountServiceTest {

    @Autowired
    private BankAccountService balanceService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        BankAccount testAccount = new BankAccount(0L, 10000L, 0L);
        bankAccountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        bankAccountRepository.deleteAll();
    }

    @Test
    void getBalance() {

    }

    @Test
    void increase() {
    }

    @Test
    void decrease() {
    }
}