package com.payments.factory;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CryptoPayment;
import com.payments.dto.payment.CryptoPaymentDTO;

public class CryptoFactory implements PaymentFactory<CryptoPaymentDTO> {
    @Override
    public CryptoPayment create(CryptoPaymentDTO dto) {
        return new CryptoPayment(dto.accountId());
    }
}
