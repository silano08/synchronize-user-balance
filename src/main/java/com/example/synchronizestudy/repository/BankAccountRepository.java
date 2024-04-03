package com.example.synchronizestudy.repository;

import com.example.synchronizestudy.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from BankAccount s where s.id = :id")
    BankAccount findByIdWithOptimisticLock(Long id);
}
