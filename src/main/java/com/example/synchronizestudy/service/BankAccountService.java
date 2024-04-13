package com.example.synchronizestudy.service;

import com.example.synchronizestudy.domain.BankAccount;
import com.example.synchronizestudy.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    // 서비스 클래스의 일부 멤버 변수로 Lock을 보관하는 Map을 추가
    private ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, AtomicInteger> concurrentDeposits = new ConcurrentHashMap<>();


    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public Long getBalance(Long id) {
        // 잔고 내역 조회
        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id).orElseThrow(() -> new IllegalStateException("Account not found"));
        return balance.getBalance();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void increase(Long id, Long quantity) throws RuntimeException {

        BankAccount account = bankAccountRepository.findByIdWithOptimisticLock(id)
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        AtomicInteger counter = concurrentDeposits.computeIfAbsent(id, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        // 최초 요청 시점에서도 동시성 검사를 수행하고 모든 요청을 거부
        if (currentCount > 1 || !tryLock(account.getId())) {
            counter.decrementAndGet(); // 카운트 감소
            throw new RuntimeException("Concurrent deposit requests are not allowed. All requests are failed.");
        }

        try {
            Thread.sleep(100); // 동시성을 더 명확하게 보여주기 위한 대기 시간
            account.increase(quantity);
            bankAccountRepository.save(account);
            throw new RuntimeException("Forced failure to ensure all transactions fail.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            releaseLock(account.getId());
            concurrentDeposits.get(id).decrementAndGet(); // 작업 완료 후 카운터 감소
        }
    }



    @Transactional
    public void decrease(Long id, Long quantity) {
        // 잔고 출금
        // 요청이 동시에 2개이상 올 경우 차례대로 실행 -> 실패했을때 재시도하도록 옵티미스틱 락 사용
        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id).orElseThrow(() -> new IllegalStateException("Account not found"));
        balance.decrease(quantity);
        bankAccountRepository.saveAndFlush(balance);
    }


    // Lock을 시도하는 메서드
    private boolean tryLock(Long id) {
        // ID별로 Lock 객체를 만들고, 이미 존재하지 않으면 새로운 ReentrantLock을 생성
        Lock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());

        // tryLock을 사용하여 즉시 Lock을 획득하려 시도
        boolean isLocked = lock.tryLock();
        if (!isLocked) {
            // Lock을 획득하지 못했다면 false 반환
            return false;
        }

        // Lock을 성공적으로 획득했다면 true 반환
        return true;
    }

    // Lock을 해제하는 메서드
    private void releaseLock(Long id) {
        // Map에서 해당 ID에 해당하는 Lock을 가져옴
        Lock lock = locks.get(id);

        if (lock == null) {
            // Lock 객체가 없는 경우는 없어야 하지만, 혹시 모르니 검사
            throw new IllegalStateException("No lock present for ID: " + id);
        }

        // Lock을 해제하고, Lock 객체를 Map에서 제거
        lock.unlock();
        locks.remove(id, lock);
    }

}
