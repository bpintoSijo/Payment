package com.payments.domain.payment;

import com.payments.domain.transaction.Transaction;
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
    void pay_nullAmount_returnFalse() {
        assertThat(payment.pay(null)).isFalse();
    }

    @Test
    void pay_negativeAmount_returnsRefusedMessage() {
        boolean result = payment.pay(new BigDecimal("-50.00"));
        assertThat(result).isFalse();
    }

    @Test
    void pay_zeroAmount_returnsPaidMessage() {
        boolean result = payment.pay(BigDecimal.ZERO);
        assertThat(result).isTrue();
    }

    @Test
    void pay_positiveAmount_returnsPaidMessage() {
        boolean result = payment.pay(new BigDecimal("100.50"));
        assertThat(result).isTrue();
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
