package com.payments.strategy.payment;

import com.payments.domain.payment.CryptoPayment;
import com.payments.dto.payment.CryptoPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import org.springframework.stereotype.Component;

@Component
public class CryptoPaymentStrategy implements PaymentStrategy<CryptoPaymentDTO, CryptoPayment>{
    @Override
    public CryptoPayment createFromDTO(CryptoPaymentDTO dto) {
        return new CryptoPayment(dto.accountId());
    }

    @Override
    public void update(CryptoPayment payment, CryptoPaymentDTO dto) {
        payment.setAccountId(dto.accountId());
    }

    @Override
    public PaymentMethodDTO toDTO(CryptoPayment payment) {
        return new CryptoPaymentDTO(payment.getId(), payment.getType(), payment.getAccountId());
    }

    @Override
    public Class<CryptoPaymentDTO> getSupportedDtoType() {
        return CryptoPaymentDTO.class;
    }

    @Override
    public Class<CryptoPayment> getSupportedPaymentType() {
        return CryptoPayment.class;
    }
}
