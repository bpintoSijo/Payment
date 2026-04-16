package com.payments.dto.transaction;

import com.payments.domain.transaction.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private Long paymentMethodId;
    private Long ownerId;

    public static TransactionDTO fromEntity(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setPaymentMethodId(transaction.getPayment().getId());
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionDTO that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(paymentMethodId, that.paymentMethodId) &&
                Objects.equals(ownerId, that.getOwnerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, paymentMethodId, ownerId);
    }
}
