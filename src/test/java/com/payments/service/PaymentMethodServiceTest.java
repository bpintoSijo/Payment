package com.payments.service;

import com.payments.domain.User;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.UserRepository;
import com.payments.strategy.payment.PaymentStrategyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentStrategyRegistry paymentStrategyRegistry;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private User buildUser(Long id) {
        User user = User.builder().username("john").email("john@test.com").password("password").build();
        user.setId(id);
        return user;
    }

    private CreditCardPayment buildPayment(Long id, String accountId, User owner) {
        CreditCardPayment payment = new CreditCardPayment(accountId);
        payment.setId(id);
        payment.setOwner(owner);
        return payment;
    }

    @Test
    void create_savesAndReturnsDTO() {
        User user = buildUser(1L);
        PaymentMethodDTO dto = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPayment payment = buildPayment(1L, "card-001", user);

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(paymentStrategyRegistry.create(dto)).thenReturn(payment);
        when(paymentStrategyRegistry.toDTO(payment)).thenReturn(dto);

        PaymentMethodDTO result = paymentMethodService.create(1L, dto);

        assertThat(result).isEqualTo(dto);
        verify(paymentMethodRepository).save(payment);
    }

    @Test
    void create_setsOwnerOnPayment() {
        User user = buildUser(1L);
        PaymentMethodDTO dto = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPayment payment = new CreditCardPayment("card-001");

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(paymentStrategyRegistry.create(dto)).thenReturn(payment);
        when(paymentStrategyRegistry.toDTO(payment)).thenReturn(dto);

        paymentMethodService.create(1L, dto);

        assertThat(payment.getOwner()).isEqualTo(user);
    }

    @Test
    void getById_existingId_returnsPayment() {
        CreditCardPayment payment = buildPayment(1L, "card-001", buildUser(1L));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThat(paymentMethodService.getById(1L)).isPresent().contains(payment);
    }

    @Test
    void getById_unknownId_returnsEmpty() {
        when(paymentMethodRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(paymentMethodService.getById(99L)).isEmpty();
    }

    @Test
    void pay_validAmount_returnsTrue() {
        User user = buildUser(1L);
        CreditCardPayment payment = buildPayment(1L, "card-001", user);

        boolean result = paymentMethodService.pay(payment, BigDecimal.TEN);

        assertThat(result).isTrue();
        verify(transactionService).create(1L, BigDecimal.TEN, 1L);
    }

    @Test
    void pay_callsTransactionServiceWithCorrectArgs() {
        User user = buildUser(1L);
        CreditCardPayment payment = buildPayment(2L, "card-001", user);

        paymentMethodService.pay(payment, new BigDecimal("50.00"));

        verify(transactionService).create(1L, new BigDecimal("50.00"), 2L);
    }

    @Test
    void getAvailablePaymentMethod_returnsAllUserPayments() {
        User user = buildUser(1L);
        CreditCardPayment p1 = buildPayment(1L, "card-001", user);
        CreditCardPayment p2 = buildPayment(2L, "card-002", user);
        PaymentMethodDTO dto1 = new CreditCardPaymentDTO(1, "CARD", "card-001");
        PaymentMethodDTO dto2 = new CreditCardPaymentDTO(2, "CARD", "card-002");

        when(paymentMethodRepository.findByOwnerId(1L)).thenReturn(List.of(p1, p2));
        when(paymentStrategyRegistry.toDTO(p1)).thenReturn(dto1);
        when(paymentStrategyRegistry.toDTO(p2)).thenReturn(dto2);

        List<PaymentMethodDTO> result = paymentMethodService.getAvailablePaymentMethod(1L);

        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
    }

    @Test
    void getAvailablePaymentMethod_noPayments_returnsEmptyList() {
        when(paymentMethodRepository.findByOwnerId(1L)).thenReturn(List.of());

        assertThat(paymentMethodService.getAvailablePaymentMethod(1L)).isEmpty();
    }
}