package com.payments.delegate.transaction;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("notifySuccessDelegate")
public class NotifySuccessDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(NotifySuccessDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String accountId = (String) execution.getVariable("accountId");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");
        String transactionRef = (String) execution.getVariable("transactionRef");
        String paymentType = (String) execution.getVariable("resolvedPaymentType");

        log.info("Payment SUCCESS - accountId={}, type={}, amount={}, ref={}",
                accountId, paymentType, amount, transactionRef);

        // TODO notification service here
        execution.setVariable("transactionStatus", "SUCCESS");
    }
}
