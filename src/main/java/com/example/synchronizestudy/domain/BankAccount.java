package com.example.synchronizestudy.domain;

import javax.persistence.*;

@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;

    @Version
    private Long version;

    public void decrease(Long balance){
        if (this.balance - balance < 0) {
            throw new RuntimeException("계좌 잔고가 모자랍니다.");
        }

        this.balance -= balance;
    }

    public void increase(Long balance){
        this.balance += balance;
    }


    public Long getbalance() {
        return balance;
    }

}
