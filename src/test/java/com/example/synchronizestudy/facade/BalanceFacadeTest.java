package com.example.synchronizestudy.facade;

import com.example.synchronizestudy.repository.BankAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BalanceFacadeTest {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void increaseSameTime() {
        // 잔고 입금 요청이 동시에 2개 이상 올 경우 실패해야함
    }

    @Test
    void decreaseSameTime() {
        //  잔고 출금 요청이 동시에 2개 이상 올 경우 차례대로 실행

    }

    @Test
    void increaseAndDecreaseSameTime() {
        //  잔고 입금과 출금 요청은 동시에 올 수 있고, 요청 온 차례대로 실행
        // 입금->출금 , 출금->입금으로 로직 작성할 것

    }
}