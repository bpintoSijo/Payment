package com.payments.domain.payment;

import com.payments.domain.transaction.Transaction;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="payment_methods")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractPaymentMethod implements Payment {
    
    @Id
    @GeneratedValue
    long id;

    @Column(name = "account_id", nullable = false, unique = true)
    String accountId;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    protected AbstractPaymentMethod() {
    }

    protected AbstractPaymentMethod(String accountId) {
        this.accountId = accountId;
    }

    // Default methods
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean addTransaction(Transaction transaction) {
        transaction.setPayment(this);
        return transactions.add(transaction);
    }

    public boolean removeTransaction(Transaction transaction) {
        transaction.setPayment(null);
        return transactions.remove(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String pay(BigDecimal amount) {
        if(amount == null) {
            throw new IllegalArgumentException("Can't pay with a null amount");
        }

        StringBuilder payMessageBuilder;
        if(BigDecimal.ZERO.compareTo(amount) > 0) {
            payMessageBuilder = new StringBuilder("Payment refused ");
        } else {
            payMessageBuilder = new StringBuilder("Paid ");
        }

        payMessageBuilder.append(amount).append(" using ").append(getType())
                .append(" - ").append(accountId);
        return payMessageBuilder.toString();
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
