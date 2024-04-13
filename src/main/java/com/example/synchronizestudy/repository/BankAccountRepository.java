package com.example.synchronizestudy.repository;

import com.example.synchronizestudy.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from BankAccount s where s.id = :id")
    Optional<BankAccount> findByIdWithOptimisticLock(Long id);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    BankAccount findByIdWithPessimisticLock(@Param("id") Long id);
}
