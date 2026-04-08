package com.payments.service;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void create_validPaymentMethod_savesAndReturnsTransaction() {
        CreditCardPayment card = new CreditCardPayment("card-001");

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(card));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.create(new BigDecimal("75.00"), 1L);

        assertThat(result.getAmount()).isEqualByComparingTo("75.00");
        assertThat(result.getPayment()).isEqualTo(card);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_unknownPaymentMethodId_throwsIllegalArgumentException() {
        when(paymentMethodRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(BigDecimal.TEN, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Payment method not found: 99");
    }
}
