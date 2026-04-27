package com.payments.service;

import com.payments.domain.User;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.transaction.Transaction;
import com.payments.exceptions.transaction.NegativeAmountException;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import com.payments.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User buildUser(Long id) {
        User user = User.builder().username("john").email("john@test.com").password("password").build();
        user.setId(id);
        return user;
    }

    private CreditCardPayment buildPayment(Long id, User owner) {
        CreditCardPayment payment = new CreditCardPayment("card-001");
        payment.setId(id);
        payment.setOwner(owner);
        return payment;
    }

    @Test
    void create_validArgs_savesAndReturnsTransaction() {
        User user = buildUser(1L);
        CreditCardPayment payment = buildPayment(1L, user);
        Transaction saved = new Transaction();
        saved.setAmount(BigDecimal.TEN);
        saved.setPayment(payment);
        saved.setOwner(user);

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(transactionRepository.save(any())).thenReturn(saved);

        Transaction result = transactionService.create(1L, BigDecimal.TEN, 1L);

        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_negativeAmount_throwsNegativeAmountException() {
        BigDecimal minusTen = new BigDecimal("-10.00");
        assertThatThrownBy(() -> transactionService.create(1L, minusTen, 1L))
                .isInstanceOf(NegativeAmountException.class)
                .hasMessageContaining("negative amount");
    }

    @Test
    void create_zeroAmount_doesNotThrow() {
        User user = buildUser(1L);
        CreditCardPayment payment = buildPayment(1L, user);

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThatCode(() -> transactionService.create(1L, BigDecimal.ZERO, 1L)).doesNotThrowAnyException();
    }

    @Test
    void create_unknownPaymentMethod_throwsEntityNotFoundException() {
        when(paymentMethodRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(1L, BigDecimal.TEN, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Payment method not found");
    }

    @Test
    void create_setsCorrectTransactionFields() {
        User user = buildUser(1L);
        CreditCardPayment payment = buildPayment(1L, user);

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.create(1L, BigDecimal.TEN, 1L);

        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getPayment()).isEqualTo(payment);
        assertThat(result.getOwner()).isEqualTo(user);
    }
}