package com.payments.repository;

import com.payments.domain.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOwnerId(Long userId);

    Page<Transaction> findByOwnerId(Long ownerId, Pageable pageable);
}