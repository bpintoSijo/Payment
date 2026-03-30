package com.payments.domain.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "paypal_payments")
@DiscriminatorValue("paypal")
public class PaypalPayment extends AbstractPaymentMethod implements Payment {
    public PaypalPayment() {

    }

    public PaypalPayment(String id) {
        super(id);
    }

    @Override
    public void pay(BigDecimal amount) {
        super.pay(amount);
    }

    @Override
    public String getType() {
        return "Paypal";
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof PaypalPayment other)) return false;

        return id == other.getId() &&
                Objects.equals(accountId, other.getAccountId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
