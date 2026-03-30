package com.payments.strategy.payment;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import org.springframework.stereotype.Component;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy<CreditCardPaymentDTO, CreditCardPayment>{
    @Override
    public CreditCardPayment createFromDTO(CreditCardPaymentDTO dto) {
        return new CreditCardPayment(dto.accountId());
    }

    @Override
    public void update(CreditCardPayment payment, CreditCardPaymentDTO dto) {
        payment.setAccountId(dto.accountId());
    }

    @Override
    public PaymentMethodDTO toDTO(CreditCardPayment payment) {
        return new CreditCardPaymentDTO(payment.getId(), payment.getType(), payment.getAccountId());
    }

    @Override
    public Class<CreditCardPaymentDTO> getSupportedDtoType() {
        return CreditCardPaymentDTO.class;
    }

    @Override
    public Class<CreditCardPayment> getSupportedPaymentType() {
        return CreditCardPayment.class;
    }
}
