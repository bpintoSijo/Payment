package com.payments.dto.transaction;

import com.payments.dto.payment.PaymentMethodDTO;

import java.math.BigDecimal;

public record TransactionDTO(long id, BigDecimal amount, PaymentMethodDTO payment) {
}
