package com.payments.domain.payment;

import java.math.BigDecimal;

public interface Payment {
    void pay(BigDecimal amount);

    String getType();
}
