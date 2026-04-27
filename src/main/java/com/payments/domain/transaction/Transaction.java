package com.payments.domain.transaction;

import com.payments.domain.User;
import com.payments.domain.payment.AbstractPaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Getter @Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private AbstractPaymentMethod payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private String status = "SUCCESS";

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Transaction other)) return false;

        return id == other.getId() &&
                Objects.compare(amount, other.getAmount(), BigDecimal::compareTo) == 0 &&
                Objects.equals(payment, other.getPayment()) &&
                Objects.equals(owner, other.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, payment);
    }
}
