package com.payments.dto.payment;

public record CreditCardPaymentDTO(long id, String type, String accountId) implements PaymentMethodDTO {
}
