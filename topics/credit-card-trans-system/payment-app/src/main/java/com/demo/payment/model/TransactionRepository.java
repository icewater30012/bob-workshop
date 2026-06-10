package com.demo.payment.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    List<Transaction> findTop20ByOrderByCreatedAtDesc();
    
    List<Transaction> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after);
}

// Made with Bob
