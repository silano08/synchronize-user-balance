package com.example.synchronizestudy.facade;

import com.example.synchronizestudy.service.BankAccountService;
import org.springframework.stereotype.Service;

@Service
public class BalanceFacade {
    private BankAccountService bankAccountService;

    public BalanceFacade(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void increase(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                bankAccountService.increase(id, quantity);

                break;
            } catch (Exception e) {
                throw new RuntimeException("입금 요청은 연달아 불가능합니다.");
            }
        }
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                bankAccountService.decrease(id, quantity);

                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
