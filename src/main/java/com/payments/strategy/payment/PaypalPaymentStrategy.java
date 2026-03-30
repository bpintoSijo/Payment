package com.payments.strategy.payment;

import com.payments.domain.payment.PaypalPayment;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.dto.payment.PaypalPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaypalPaymentStrategy implements PaymentStrategy<PaypalPaymentDTO, PaypalPayment> {
    @Override
    public PaypalPayment createFromDTO(PaypalPaymentDTO dto) {
        return new PaypalPayment(dto.accountId());
    }

    @Override
    public void update(PaypalPayment payment, PaypalPaymentDTO dto) {
        payment.setAccountId(dto.accountId());
    }

    @Override
    public PaymentMethodDTO toDTO(PaypalPayment payment) {
        return new PaypalPaymentDTO(payment.getId(), payment.getType(), payment.getAccountId());
    }

    @Override
    public Class<PaypalPaymentDTO> getSupportedDtoType() {
        return PaypalPaymentDTO.class;
    }

    @Override
    public Class<PaypalPayment> getSupportedPaymentType() {
        return PaypalPayment.class;
    }
}
