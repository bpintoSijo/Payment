package com.payments.strategy.payment;

import com.payments.domain.payment.PaypalPayment;
import com.payments.dto.payment.PaypalPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaypalPaymentStrategyTest {

    private final PaypalPaymentStrategy strategy = new PaypalPaymentStrategy();

    @Test
    void createFromDTO_returnsPaymentWithCorrectAccountId() {
        PaypalPaymentDTO dto = new PaypalPaymentDTO(0, "PAYPAL", "paypal@test.com");
        PaypalPayment payment = strategy.createFromDTO(dto);
        assertThat(payment.getAccountId()).isEqualTo("paypal@test.com");
        assertThat(payment.getType()).isEqualTo("PAYPAL");
    }

    @Test
    void update_modifiesAccountId() {
        PaypalPayment payment = new PaypalPayment("old@test.com");
        strategy.update(payment, new PaypalPaymentDTO(0, "PAYPAL", "new@test.com"));
        assertThat(payment.getAccountId()).isEqualTo("new@test.com");
    }

    @Test
    void toDTO_returnsCorrectDTO() {
        PaypalPayment payment = new PaypalPayment("paypal@test.com");
        PaymentMethodDTO dto = strategy.toDTO(payment);
        assertThat(dto).isInstanceOf(PaypalPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("PAYPAL");
        assertThat(dto.accountId()).isEqualTo("paypal@test.com");
    }

    @Test
    void getSupportedDtoType_returnsPaypalPaymentDTO() {
        assertThat(strategy.getSupportedDtoType()).isEqualTo(PaypalPaymentDTO.class);
    }

    @Test
    void getSupportedPaymentType_returnsPaypalPayment() {
        assertThat(strategy.getSupportedPaymentType()).isEqualTo(PaypalPayment.class);
    }
}
