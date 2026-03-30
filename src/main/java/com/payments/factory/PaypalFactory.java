package com.payments.factory;

import com.payments.domain.payment.PaypalPayment;
import com.payments.dto.payment.PaypalPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaypalFactory implements PaymentFactory<PaypalPaymentDTO> {
    @Override
    public PaypalPayment create(PaypalPaymentDTO dto) {
        return new PaypalPayment(dto.accountId());
    }

    @Override
    public Class<PaypalPaymentDTO> getSupportedType() {
        return PaypalPaymentDTO.class;
    }
}
