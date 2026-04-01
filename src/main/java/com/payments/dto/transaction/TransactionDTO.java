package com.payments.dto.transaction;

import com.payments.domain.transaction.Transaction;

import java.math.BigDecimal;

public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private Long paymentMethodId;

    public TransactionDTO() {
        // Empty to used with SpringBoot
    }

    public static TransactionDTO fromEntity(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
