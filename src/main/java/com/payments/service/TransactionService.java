package com.payments.service;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public TransactionService(TransactionRepository transactionRepository, PaymentMethodRepository paymentMethodRepository) {
        this.transactionRepository = transactionRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Transactional
    public TransactionDTO create(TransactionDTO form) {
        AbstractPaymentMethod paymentMethod = paymentMethodRepository.findById(form.getPaymentMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found: " + form.getPaymentMethodId()));

        Transaction transaction = new Transaction();
        transaction.setAmount(form.getAmount());
        transaction.setPayment(paymentMethod);

        transactionRepository.save(transaction);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setPaymentMethodId(paymentMethod.getId());
        return transactionDTO;
    }
}