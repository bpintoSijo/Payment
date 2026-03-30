package com.payments.mapper;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.payment.PaypalPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.CryptoPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.dto.payment.PaypalPaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    private CreditCardPaymentDTO toDTO(CreditCardPayment creditCardPayment) {
        return new CreditCardPaymentDTO(creditCardPayment.getId(), creditCardPayment.getType(), creditCardPayment.getAccountId());
    }

    private CryptoPaymentDTO toDTO(CryptoPayment cryptoPayment) {
        return new CryptoPaymentDTO(cryptoPayment.getId(), cryptoPayment.getType(), cryptoPayment.getAccountId());
    }

    private PaypalPaymentDTO toDTO(PaypalPayment paypalPayment) {
        return new PaypalPaymentDTO(paypalPayment.getId(), paypalPayment.getType(), paypalPayment.getAccountId());
    }

    public PaymentMethodDTO toDTO(AbstractPaymentMethod payment) {
        return switch (payment) {
            case CreditCardPayment creditCardPayment    -> toDTO(creditCardPayment);
            case CryptoPayment cryptoPayment    -> toDTO(cryptoPayment);
            case PaypalPayment paypalPayment    -> toDTO(paypalPayment);
            default -> throw new IllegalArgumentException("Unknown type : " + payment.getClass());
        };
    }
}