package com.payments.dto.payment;

public record CryptoPaymentDTO(long id, String type, String accountId) implements PaymentMethodDTO {
}
