package com.payments.service;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.transaction.Transaction;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public TransactionService(TransactionRepository transactionRepository, PaymentMethodRepository paymentMethodRepository) {
        this.transactionRepository = transactionRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Transactional
    public Transaction create(BigDecimal amount, long paymentMethodId) {
        AbstractPaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found: " + paymentMethodId));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setPayment(paymentMethod);

        return transactionRepository.save(transaction);
    }
}