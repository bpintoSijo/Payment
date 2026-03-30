package com.payments.domain.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "credit_card_payments")
@DiscriminatorValue("credit_card")
public class CreditCardPayment extends AbstractPaymentMethod implements Payment {

    public CreditCardPayment() {
    }

    public CreditCardPayment(String accountId) {
        super(accountId);
    }

    @Override
    public String getType() {
        return "CARD";
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof CreditCardPayment other)) return false;

        return id == other.getId() &&
                Objects.equals(accountId, other.getAccountId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
