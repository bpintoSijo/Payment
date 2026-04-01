package com.payments.domain.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AbstractPaymentMethodTest {

    private final AbstractPaymentMethod payment = new AbstractPaymentMethod() {
        @Override
        public String getType() {
            return "ABSTRACT";
        }
    };

    @Test
    void pay_nullAmount_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> payment.pay(null))
                .isInstanceOf(IllegalArgumentException.class)
                .isEqualTo(false);
    }

    @Test
    void pay_negativeAmount_returnsRefusedMessage() {
        boolean result = payment.pay(new BigDecimal("-50.00"));
        assertThat(result).isFalse();
    }

    @Test
    void pay_zeroAmount_returnsPaidMessage() {
        boolean result = payment.pay(BigDecimal.ZERO);
        assertThat(result).isFalse();
    }

    @Test
    void pay_positiveAmount_returnsPaidMessage() {
        boolean result = payment.pay(new BigDecimal("100.50"));
        assertThat(result).isTrue();
    }

    @Test
    void addTransaction_linksPaymentToTransaction() {
        com.payments.domain.transaction.Transaction tx = new com.payments.domain.transaction.Transaction();
        payment.addTransaction(tx);
        assertThat(payment.getTransactions()).contains(tx);
        assertThat(tx.getPayment()).isEqualTo(payment);
    }

    @Test
    void removeTransaction_unlinksTransaction() {
        com.payments.domain.transaction.Transaction tx = new com.payments.domain.transaction.Transaction();
        payment.addTransaction(tx);
        payment.removeTransaction(tx);
        assertThat(payment.getTransactions()).doesNotContain(tx);
        assertThat(tx.getPayment()).isNull();
    }

    @Test
    void equals_sameIdAndAccountId_returnsTrue() {
        CreditCardPayment a = new CreditCardPayment("card-001");
        CreditCardPayment b = new CreditCardPayment("card-001");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_differentAccountId_returnsFalse() {
        CreditCardPayment a = new CreditCardPayment("card-001");
        CreditCardPayment b = new CreditCardPayment("card-002");
        assertThat(a).isNotEqualTo(b);
    }
}
