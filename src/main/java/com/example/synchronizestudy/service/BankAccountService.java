package com.example.synchronizestudy.service;

import com.example.synchronizestudy.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    private BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    private Long getBalance(){
        // 잔고 내역 조회
        return null;
    }

    private Long increase(){
        // 잔고 입금
        // 요청이 동시에 2개이상 올 경우 실패 -> 실패했을때 실패라고 출력,옵티미스틱 락 사용
        // 출금과 입금요청이 동시에오면 요청온 차례대로 실행
        return null;
    }

    private Long decrease(){
        // 잔고 출금
        // 요청이 동시에 2개이상 올 경우 차례대로 실행 -> 실패했을때 재시도하도록 옵티미스틱 락 사용
        return null;
    }
}
