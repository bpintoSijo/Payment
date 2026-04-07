package com.payments.strategy.payment;

import com.payments.domain.payment.CryptoPayment;
import com.payments.dto.payment.CryptoPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CryptoPaymentStrategyTest {

    private final CryptoPaymentStrategy strategy = new CryptoPaymentStrategy();

    @Test
    void createFromDTO_returnsPaymentWithCorrectAccountId() {
        CryptoPaymentDTO dto = new CryptoPaymentDTO(0, "CRYPTO", "0xABCD1234");
        CryptoPayment payment = strategy.createFromDTO(dto);
        assertThat(payment.getAccountId()).isEqualTo("0xABCD1234");
        assertThat(payment.getType()).isEqualTo("CRYPTO");
    }

    @Test
    void update_modifiesAccountId() {
        CryptoPayment payment = new CryptoPayment("0xOLD");
        strategy.update(payment, new CryptoPaymentDTO(0, "CRYPTO", "0xNEW"));
        assertThat(payment.getAccountId()).isEqualTo("0xNEW");
    }

    @Test
    void toDTO_returnsCorrectDTO() {
        CryptoPayment payment = new CryptoPayment("0xWALLET");
        PaymentMethodDTO dto = strategy.toDTO(payment);
        assertThat(dto).isInstanceOf(CryptoPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("CRYPTO");
        assertThat(dto.accountId()).isEqualTo("0xWALLET");
    }

    @Test
    void getSupportedDtoType_returnsCryptoPaymentDTO() {
        assertThat(strategy.getSupportedDtoType()).isEqualTo(CryptoPaymentDTO.class);
    }

    @Test
    void getSupportedPaymentType_returnsCryptoPayment() {
        assertThat(strategy.getSupportedPaymentType()).isEqualTo(CryptoPayment.class);
    }
}
