package com.payments.domain.payment;

import java.math.BigDecimal;

public interface Payment {
    boolean pay(BigDecimal amount);

    String getType();
}
