package com.payments.dto.transaction;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionDTOTest {

    @Test
    void create_using_fromEntity() {
        AbstractPaymentMethod payment = new CryptoPayment("0xABCD1234");
        payment.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(0);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setPayment(new CryptoPayment("0xABCD1234"));
        TransactionDTO dto = TransactionDTO.fromEntity(transaction);

        assertThat(dto).isInstanceOf(TransactionDTO.class);
        assertThat(dto.getId()).isEqualTo(transaction.getId());
        assertThat(dto.getPaymentMethodId()).isEqualTo(payment.getId());
        assertThat(dto.getAmount()).isEqualTo(BigDecimal.TEN);
    }
}
