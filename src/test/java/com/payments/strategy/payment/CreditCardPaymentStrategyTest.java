package com.payments.strategy.payment;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CreditCardPaymentStrategyTest {

    private final CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy();

    @Test
    void createFromDTO_returnsPaymentWithCorrectAccountId() {
        CreditCardPaymentDTO dto = new CreditCardPaymentDTO(0, "CARD", "card-abc");
        CreditCardPayment payment = strategy.createFromDTO(dto);
        assertThat(payment.getAccountId()).isEqualTo("card-abc");
        assertThat(payment.getType()).isEqualTo("CARD");
    }

    @Test
    void update_modifiesAccountId() {
        CreditCardPayment payment = new CreditCardPayment("old-id");
        strategy.update(payment, new CreditCardPaymentDTO(0, "CARD", "new-id"));
        assertThat(payment.getAccountId()).isEqualTo("new-id");
    }

    @Test
    void toDTO_returnsCorrectDTO() {
        CreditCardPayment payment = new CreditCardPayment("card-xyz");
        PaymentMethodDTO dto = strategy.toDTO(payment);
        assertThat(dto).isInstanceOf(CreditCardPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("CARD");
        assertThat(dto.accountId()).isEqualTo("card-xyz");
    }

    @Test
    void getSupportedDtoType_returnsCreditCardPaymentDTO() {
        assertThat(strategy.getSupportedDtoType()).isEqualTo(CreditCardPaymentDTO.class);
    }

    @Test
    void getSupportedPaymentType_returnsCreditCardPayment() {
        assertThat(strategy.getSupportedPaymentType()).isEqualTo(CreditCardPayment.class);
    }
}
