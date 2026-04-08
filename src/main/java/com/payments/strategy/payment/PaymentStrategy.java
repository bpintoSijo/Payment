package com.payments.strategy.payment;

import com.payments.domain.payment.Payment;
import com.payments.dto.payment.PaymentMethodDTO;

public interface PaymentStrategy<T extends PaymentMethodDTO, P extends Payment> {
    P createFromDTO(T dto);
    void update(P payment, T dto);
    PaymentMethodDTO toDTO(P payment);
    Class<T> getSupportedDtoType();
    Class<P> getSupportedPaymentType();
}
