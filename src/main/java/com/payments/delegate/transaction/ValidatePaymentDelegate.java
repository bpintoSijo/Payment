package com.payments.delegate.transaction;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("validatePaymentDelegate")
public class ValidatePaymentDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ValidatePaymentDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String accountId = (String) execution.getVariable("accountId");
        String methodType = (String) execution.getVariable("paymentMethodType");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");

        log.info("Validating payment - accountId={}, type={}, amount={}", accountId, methodType, amount);

        boolean valid = accountId != null && !accountId.isBlank()
                     && methodType != null && !methodType.isBlank()
                     && amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;

        if (!valid) {
            String reason = buildReason(accountId, methodType, amount);
            log.warn("Validation failed - {}", reason);
            execution.setVariable("failureReason", reason);
        }

        execution.setVariable("dataValid", valid);
    }

    private String buildReason(String accountId, String methodType, BigDecimal amount) {
        if (accountId == null || accountId.isBlank()) return "Missing accountId";
        if (methodType == null || methodType.isBlank()) return "Missing payment method type";
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return "Amount must be greater than zero";
        return "Unknown validation error";
    }
}
