package com.payments.factory;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.payment.PaymentMethodDTO;

public interface PaymentFactory<T extends PaymentMethodDTO> {
    AbstractPaymentMethod create(T dto);
}
