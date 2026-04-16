package com.payments.domain.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "crypto_payments")
@DiscriminatorValue("crypto")
@NoArgsConstructor
public class CryptoPayment extends AbstractPaymentMethod implements Payment {
    public CryptoPayment(String accountId) {
        super(accountId);
    }

    @Override
    public String getType() {
        return "CRYPTO";
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof CryptoPayment other)) return false;

        return id == other.getId() &&
                Objects.equals(accountId, other.getAccountId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
