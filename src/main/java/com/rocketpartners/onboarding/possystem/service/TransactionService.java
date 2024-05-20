package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void saveTransaction(Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }
}
