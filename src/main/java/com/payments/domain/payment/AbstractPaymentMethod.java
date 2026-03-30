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

    public AbstractPaymentMethod() {
    }

    public AbstractPaymentMethod(String accountId) {
        this.accountId = accountId;
    }

    // Abstract methods
    public abstract String getType();

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
    public void pay(BigDecimal amount) {
        if(amount == null) {
            throw new IllegalArgumentException("Can't pay with a null amount");
        }

        StringBuilder payMessageBuilder;
        if(BigDecimal.ZERO.compareTo(amount) > 0) {
            payMessageBuilder = new StringBuilder("Payment refused ");
        } else {
            payMessageBuilder = new StringBuilder("Paid ");
        }

        payMessageBuilder.append(amount).append(" using ").append(getType());
        System.out.println(payMessageBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
