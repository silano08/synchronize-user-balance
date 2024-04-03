package com.example.synchronizestudy.service;

import com.example.synchronizestudy.domain.BankAccount;
import com.example.synchronizestudy.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAccountService {

    private BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public Long getBalance(Long id){
        // 잔고 내역 조회

        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id);
        return balance.getbalance();
    }

    @Transactional
    public void increase(Long id, Long quantity){
        // 잔고 입금
        // 요청이 동시에 2개이상 올 경우 실패 -> 실패했을때 실패라고 출력,옵티미스틱 락 사용
        // 출금과 입금요청이 동시에오면 요청온 차례대로 실행

        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id);
        balance.decrease(quantity);

        bankAccountRepository.saveAndFlush(balance);
    }

    @Transactional
    public void decrease(Long id, Long quantity){
        // 잔고 출금
        // 요청이 동시에 2개이상 올 경우 차례대로 실행 -> 실패했을때 재시도하도록 옵티미스틱 락 사용

        BankAccount balance = bankAccountRepository.findByIdWithOptimisticLock(id);
        balance.increase(quantity);

        bankAccountRepository.saveAndFlush(balance);

    }
}
