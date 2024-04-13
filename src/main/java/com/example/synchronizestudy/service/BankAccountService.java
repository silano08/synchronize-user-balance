package com.example.synchronizestudy.service;

import com.example.synchronizestudy.domain.BankAccount;
import com.example.synchronizestudy.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final ConcurrentHashMap<Long, ReentrantLock > locks = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock(); // 어플리케이션 레벨에서 동기화를 구현할때 사용

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public Long getBalance(Long id){
        // 잔고 내역 조회
        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id);
        return balance.getBalance();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void increase(Long id, Long quantity) throws RuntimeException {

        // 잔고 입금
        // 요청이 동시에 2개이상 올 경우 실패 -> 실패했을때 실패라고 출력,옵티미스틱 락 사용
        // 출금과 입금요청이 동시에오면 요청온 차례대로 실행
        // 락을 획득하는 로직 추가
        BankAccount account = bankAccountRepository.findById(id).orElseThrow(() -> new IllegalStateException("Account not found"));

        account.increase(quantity);
        bankAccountRepository.save(account);
    }


    @Transactional
    public void decrease(Long id, Long quantity){
        // 잔고 출금
        // 요청이 동시에 2개이상 올 경우 차례대로 실행 -> 실패했을때 재시도하도록 옵티미스틱 락 사용
        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id);
        balance.decrease(quantity);
        bankAccountRepository.saveAndFlush(balance);
    }

    public boolean acquireLock(Long id) {
        // 맵에서 잠금 객체를 얻거나 새로 생성합니다.
        Lock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());
        return lock.tryLock();  // tryLock을 사용하여 잠금 시도, 잠금 가능하면 true 반환
    }

    public void releaseLock(Long id) {
        ReentrantLock lock = locks.get(id);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();  // 현재 스레드가 잠금을 보유하고 있다면 잠금을 해제합니다.
        }
    }
}
