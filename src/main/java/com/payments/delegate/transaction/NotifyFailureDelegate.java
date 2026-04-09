package com.payments.delegate.transaction;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("notifyFailureDelegate")
public class NotifyFailureDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(NotifyFailureDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String accountId = (String) execution.getVariable("accountId");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");
        String failureReason = (String) execution.getVariable("failureReason");
        String paymentType = (String) execution.getVariable("resolvedPaymentType");

        log.warn("Payment FAILED - accountId={}, type={}, amount={}, reason={}",
                accountId, paymentType, amount, failureReason);

        //TODO Notification service here

        execution.setVariable("transactionStatus", "FAILED");
    }
}
