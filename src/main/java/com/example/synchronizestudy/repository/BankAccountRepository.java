package com.example.synchronizestudy.repository;

import com.example.synchronizestudy.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
}
