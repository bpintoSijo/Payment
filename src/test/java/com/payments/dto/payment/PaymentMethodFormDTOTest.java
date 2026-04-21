package com.payments.dto.payment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaymentMethodFormDTOTest {

    @Test
    void toDTO_cardType_returnsCreditCardPaymentDTO() {
        PaymentMethodFormDTO form = formWith("CARD", "card-001");
        PaymentMethodDTO dto = form.toDTO();
        assertThat(dto).isInstanceOf(CreditCardPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("CARD");
        assertThat(dto.accountId()).isEqualTo("card-001");
    }

    @Test
    void toDTO_paypalType_returnsPaypalPaymentDTO() {
        PaymentMethodFormDTO form = formWith("PAYPAL", "paypal@email.com");
        PaymentMethodDTO dto = form.toDTO();
        assertThat(dto).isInstanceOf(PaypalPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("PAYPAL");
        assertThat(dto.accountId()).isEqualTo("paypal@email.com");
    }

    @Test
    void toDTO_cryptoType_returnsCryptoPaymentDTO() {
        PaymentMethodFormDTO form = formWith("CRYPTO", "0xABCD1234");
        PaymentMethodDTO dto = form.toDTO();
        assertThat(dto).isInstanceOf(CryptoPaymentDTO.class);
        assertThat(dto.type()).isEqualTo("CRYPTO");
        assertThat(dto.accountId()).isEqualTo("0xABCD1234");
    }

    @Test
    void toDTO_unknownType_throwsIllegalArgumentException() {
        PaymentMethodFormDTO form = formWith("BITCOIN", "acc-001");
        assertThatThrownBy(form::toDTO)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown payment type: BITCOIN");
    }

    @Test
    void toDTO_idIsZero_forAllTypes() {
        assertThat(formWith("CARD",   "card-001").toDTO().id()).isZero();
        assertThat(formWith("PAYPAL", "pp@x.com").toDTO().id()).isZero();
        assertThat(formWith("CRYPTO", "0xABC").toDTO().id()).isZero();
    }

    @Test
    void getType_returnsSetValue() {
        PaymentMethodFormDTO form = formWith("CARD", "card-001");
        assertThat(form.getType()).isEqualTo("CARD");
    }

    @Test
    void getAccountId_returnsSetValue() {
        PaymentMethodFormDTO form = formWith("CARD", "card-001");
        assertThat(form.getAccountId()).isEqualTo("card-001");
    }

    @Test
    void toDTO_nullType_throwsNullPointerException() {
        PaymentMethodFormDTO form = formWith(null, "acc-001");
        assertThatThrownBy(form::toDTO)
                .isInstanceOf(NullPointerException.class);
    }

    private PaymentMethodFormDTO formWith(String type, String accountId) {
        PaymentMethodFormDTO form = new PaymentMethodFormDTO();
        form.setType(type);
        form.setAccountId(accountId);
        return form;
    }
}