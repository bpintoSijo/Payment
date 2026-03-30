package com.payments.domain.transaction;

import com.payments.domain.payment.AbstractPaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private AbstractPaymentMethod payment;

    public Transaction() {
        // Empty constructor for Spring boot
    }

    public long getId() {
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

    public AbstractPaymentMethod getPayment() {
        return payment;
    }
    public void setPayment(AbstractPaymentMethod payment) {
        this.payment = payment;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Transaction other)) return false;

        return id == other.getId() &&
                Objects.compare(amount, other.getAmount(), BigDecimal::compareTo) == 0 &&
                Objects.equals(payment, other.getPayment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, payment);
    }
}
