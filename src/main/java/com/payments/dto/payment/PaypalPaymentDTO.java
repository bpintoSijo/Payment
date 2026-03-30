package com.payments.dto.payment;

public record PaypalPaymentDTO (long id, String type, String accountId) implements PaymentMethodDTO {
}
