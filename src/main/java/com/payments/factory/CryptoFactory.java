package com.payments.factory;

import com.payments.domain.payment.CryptoPayment;
import com.payments.dto.payment.CryptoPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class CryptoFactory implements PaymentFactory<CryptoPaymentDTO> {
    @Override
    public CryptoPayment create(CryptoPaymentDTO dto) {
        return new CryptoPayment(dto.accountId());
    }

    @Override
    public Class<CryptoPaymentDTO> getSupportedType() {
        return CryptoPaymentDTO.class;
    }
}
