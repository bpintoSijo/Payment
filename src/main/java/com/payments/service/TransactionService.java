package com.payments.service;

import com.payments.domain.User;
import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.transaction.Transaction;
import com.payments.exceptions.transaction.NegativeAmountException;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import com.payments.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Transactional
    public Transaction create(Long userId, BigDecimal amount, long paymentMethodId) {
        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Could not pay with negative amount.");
        }

        AbstractPaymentMethod paymentMethod = paymentMethodRepository.findByIdAndOwnerId(paymentMethodId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));
        User user = userRepository.getReferenceById(userId);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setPayment(paymentMethod);
        transaction.setOwner(user);

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getPageFromUserSortByCreationDate(int page, int size, Long userId) {
        return transactionRepository.findByOwnerId(userId,
                        PageRequest.of(page, size, Sort.by("createdAt").descending())
                );
    }
}