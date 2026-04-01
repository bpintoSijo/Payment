package com.payments.dto.payment;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCardPaymentDTO.class, name = "CARD"),
    @JsonSubTypes.Type(value = CryptoPaymentDTO.class, name = "CRYPTO"),
    @JsonSubTypes.Type(value = PaypalPaymentDTO.class, name = "PAYPAL"),
})
public interface PaymentMethodDTO {
    long id();
    String type();
    String accountId();
}