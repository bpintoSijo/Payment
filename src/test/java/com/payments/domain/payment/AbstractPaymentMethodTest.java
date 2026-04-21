package com.payments.domain.payment;

import com.payments.exceptions.transaction.NegativeAmountException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class AbstractPaymentMethodTest {

    private final AbstractPaymentMethod payment = new AbstractPaymentMethod("acc-001") {
        @Override
        public String getType() {
            return "ABSTRACT";
        }
    };

    @Test
    void pay_nullAmount_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> payment.pay(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not pay with a Null amount");
    }

    @Test
    void pay_negativeAmount_throwsNegativeAmountException() {
        BigDecimal amount = new BigDecimal("-50.00");
        assertThatThrownBy(() -> payment.pay(amount))
                .isInstanceOf(NegativeAmountException.class)
                .hasMessageContaining("-50.00");
    }

    @Test
    void pay_zeroAmount_returnsTrue() {
        assertThat(payment.pay(BigDecimal.ZERO)).isTrue();
    }

    @Test
    void pay_positiveAmount_returnsTrue() {
        assertThat(payment.pay(new BigDecimal("100.50"))).isTrue();
    }

    @Test
    void equals_sameInstance_returnsTrue() {
        assertThat(payment).isEqualTo(payment);
    }

    @Test
    void equals_nullObject_returnsFalse() {
        assertThat(payment).isNotEqualTo(null);
    }

    @Test
    void equals_differentType_returnsFalse() {
        assertThat(payment).isNotEqualTo("not a payment");
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

    @Test
    void hashCode_sameAccountId_returnsSameHash() {
        CreditCardPayment a = new CreditCardPayment("card-001");
        CreditCardPayment b = new CreditCardPayment("card-001");
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void hashCode_differentAccountId_returnsDifferentHash() {
        CreditCardPayment a = new CreditCardPayment("card-001");
        CreditCardPayment b = new CreditCardPayment("card-002");
        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
    }

    @Test
    void getAccountId_returnsExpectedValue() {
        assertThat(payment.getAccountId()).isEqualTo("acc-001");
    }
}