package com.payments.domain.payment;

import com.payments.domain.User;
import com.payments.exceptions.transaction.NegativeAmountException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name="payment_methods")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
@RequiredArgsConstructor
@Getter @Setter
public abstract class AbstractPaymentMethod implements Payment {
    @Id
    @GeneratedValue
    long id;

    @Column(name = "account_id", nullable = false, unique = true)
    String accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    protected AbstractPaymentMethod(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean pay(BigDecimal amount) {
        if(amount == null) {
            throw new IllegalArgumentException("Could not pay with a Null amount");
        }

        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Could not pay with a Negative amount: " + amount);
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof AbstractPaymentMethod other)) return false;

        return id == other.getId() &&
                Objects.equals(accountId, other.getAccountId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId);
    }
}
