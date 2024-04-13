package com.example.synchronizestudy.facade;

import com.example.synchronizestudy.domain.BankAccount;
import com.example.synchronizestudy.repository.BankAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("test")
class BalanceFacadeTest {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        BankAccount testAccount = new BankAccount(0L,10000L,0L);
        bankAccountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        bankAccountRepository.deleteAll();
    }


    @Test
    void increaseSameTime() throws Exception {
        Long accountId = bankAccountRepository.findAll().get(0).getId();
        int threadCount = 2; // 동시에 2개의 입금 요청
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    balanceFacade.increase(accountId, 1000L);
                    System.out.println("Deposit successful");
                } catch (Exception e) {
                    System.out.println("Deposit failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 입금 요청이 완료될 때까지 대기
        executor.shutdown();

        BankAccount updatedAccount = bankAccountRepository.findById(accountId).orElseThrow();
        assertEquals(10000L, updatedAccount.getBalance()); // 동시 입금 요청에 대한 실패 예상으로, 잔액은 변하지 않아야 함
    }


    @Test
    void decreaseSameTime() throws Exception {
        //  잔고 출금 요청이 동시에 2개 이상 올 경우 차례대로 실행
        Long accountId = bankAccountRepository.findAll().get(0).getId();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> withdrawal1 = CompletableFuture.runAsync(() -> {
            try {
                balanceFacade.decrease(accountId, 1000L);
                System.out.println("Withdrawal 1 successful");
            } catch (Exception e) {
                System.out.println("Withdrawal 1 failed: " + e.getMessage());
            }
        }, executor);

        CompletableFuture<Void> withdrawal2 = CompletableFuture.runAsync(() -> {
            try {
                balanceFacade.decrease(accountId, 1000L);
                System.out.println("Withdrawal 2 successful");
            } catch (Exception e) {
                System.out.println("Withdrawal 2 failed: " + e.getMessage());
            }
        }, executor);

        CompletableFuture.allOf(withdrawal1, withdrawal2).join();
        executor.shutdown();

        BankAccount updatedAccount = bankAccountRepository.findById(accountId).orElseThrow();
        assertEquals(8000L, updatedAccount.getBalance());
    }


    @Test
    void increaseAndDecreaseSameTime() throws Exception {
        //  잔고 입금과 출금 요청은 동시에 올 수 있고, 요청 온 차례대로 실행
        // 입금->출금 , 출금->입금으로 로직 작성할 것

        Long accountId = bankAccountRepository.findAll().get(0).getId();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> deposit = CompletableFuture.runAsync(() -> {
            try {
                balanceFacade.increase(accountId, 1000L);
                System.out.println("Deposit successful");
            } catch (Exception e) {
                System.out.println("Deposit failed: " + e.getMessage());
            }
        }, executor);

        CompletableFuture<Void> withdrawal = CompletableFuture.runAsync(() -> {
            try {
                balanceFacade.decrease(accountId, 1000L);
                System.out.println("Withdrawal successful");
            } catch (Exception e) {
                System.out.println("Withdrawal failed: " + e.getMessage());
            }
        }, executor);

        CompletableFuture.allOf(deposit, withdrawal).join();
        executor.shutdown();

        BankAccount updatedAccount = bankAccountRepository.findById(accountId).orElseThrow();
        assertEquals(10000L, updatedAccount.getBalance());
    }

}