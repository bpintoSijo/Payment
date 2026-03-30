package com.payments.factory;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;

public class CreditCardFactory implements PaymentFactory<CreditCardPaymentDTO> {
    @Override
    public CreditCardPayment create(CreditCardPaymentDTO dto) {
        return new CreditCardPayment(dto.accountId());
    }
}
