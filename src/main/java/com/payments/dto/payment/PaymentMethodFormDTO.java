package com.payments.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class PaymentMethodFormDTO {
    private String type;
    private String accountId;

    public PaymentMethodDTO toDTO() {
        return switch (type) {
            case "CARD"   -> new CreditCardPaymentDTO(0, type, accountId);
            case "PAYPAL" -> new PaypalPaymentDTO(0, type, accountId);
            case "CRYPTO" -> new CryptoPaymentDTO(0, type, accountId);
            default -> throw new IllegalArgumentException("Unknown payment type: " + type);
        };
    }
}