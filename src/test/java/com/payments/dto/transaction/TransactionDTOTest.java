package com.payments.dto.transaction;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionDTOTest {

    private TransactionDTO buildDTO(Long id, BigDecimal amount, Long paymentMethodId) {
        return new TransactionDTO(id, amount, paymentMethodId);
    }

    private Transaction buildTransaction(Long id, BigDecimal amount, AbstractPaymentMethod payment) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setPayment(payment);
        return transaction;
    }

    private AbstractPaymentMethod buildPayment(Long id, String accountId) {
        AbstractPaymentMethod payment = new CryptoPayment(accountId);
        payment.setId(id);
        return payment;
    }

    @Test
    void fromEntity_returnsCorrectDTO() {
        AbstractPaymentMethod payment = buildPayment(1L, "0xABCD1234");
        Transaction transaction = buildTransaction(0L, BigDecimal.TEN, payment);
        TransactionDTO dto = TransactionDTO.fromEntity(transaction);

        assertThat(dto).isInstanceOf(TransactionDTO.class);
        assertThat(dto.getId()).isZero();
        assertThat(dto.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(dto.getPaymentMethodId()).isEqualTo(1L);
    }

    @Test
    void equals_samFields_returnsTrue() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(1L, BigDecimal.TEN, 2L);
        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_differentId_returnsFalse() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(2L, BigDecimal.TEN, 2L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_differentAmount_returnsFalse() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(1L, BigDecimal.ONE, 2L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_differentPaymentMethodId_returnsFalse() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(1L, BigDecimal.TEN, 3L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_sameInstance_returnsTrue() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        assertThat(a).isEqualTo(a);
    }

    @Test
    void equals_null_returnsFalse() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        assertThat(a).isNotEqualTo(null);
    }

    @Test
    void hashCode_sameFields_returnsSameHash() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(1L, BigDecimal.TEN, 2L);
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void hashCode_differentFields_returnsDifferentHash() {
        TransactionDTO a = buildDTO(1L, BigDecimal.TEN, 2L);
        TransactionDTO b = buildDTO(2L, BigDecimal.ONE, 3L);
        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
    }

    @Test
    void gettersAndSetters_workCorrectly() {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(5L);
        dto.setAmount(new BigDecimal("99.99"));
        dto.setPaymentMethodId(10L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(dto.getPaymentMethodId()).isEqualTo(10L);
    }
}