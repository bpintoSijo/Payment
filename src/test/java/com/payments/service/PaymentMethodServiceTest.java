package com.payments.service;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.strategy.payment.PaymentStrategyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentStrategyRegistry paymentStrategyRegistry;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    @Test
    void create_delegatesToRegistryAndPersistsEntity() {
        CreditCardPaymentDTO inputDto = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPayment entity = new CreditCardPayment("card-001");
        CreditCardPaymentDTO savedDto = new CreditCardPaymentDTO(1, "CARD", "card-001");

        when(paymentStrategyRegistry.create(inputDto)).thenReturn(entity);
        when(paymentStrategyRegistry.toDTO(entity)).thenReturn(savedDto);

        PaymentMethodDTO result = paymentMethodService.create(0L, inputDto);

        assertThat(result).isEqualTo(savedDto);
        verify(paymentMethodRepository).save(entity);
    }

    @Test
    void getAvailablePaymentMethod_returnsMappedDTOs() {
        CreditCardPayment entity = new CreditCardPayment("card-001");
        CreditCardPaymentDTO dto = new CreditCardPaymentDTO(1, "CARD", "card-001");

        when(paymentMethodRepository.findAll()).thenReturn(List.of(entity));
        when(paymentStrategyRegistry.toDTO(entity)).thenReturn(dto);

        List<PaymentMethodDTO> result = paymentMethodService.getAvailablePaymentMethod(0L);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void getAvailablePaymentMethod_emptyRepository_returnsEmptyList() {
        when(paymentMethodRepository.findByOwnerId(anyLong())).thenReturn(List.of());

        List<PaymentMethodDTO> result = paymentMethodService.getAvailablePaymentMethod(anyLong());

        assertThat(result).isEmpty();
    }
}
