package com.payments.delegate.transaction;


import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.Payment;
import com.payments.service.PaymentMethodService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component("checkFundsDelegate")
public class CheckFundsDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckFundsDelegate.class);

    private final PaymentMethodService paymentMethodService;

    public CheckFundsDelegate(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String accountId = (String) execution.getVariable("accountId");
        Long paymentMethodId = (Long) execution.getVariable("paymentMethodId");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");

        log.info("Checking funds - accountId={}, paymentMethodId={}, amount={}", accountId, paymentMethodId, amount);

        Optional<AbstractPaymentMethod> optionalPaymentMethod = paymentMethodService.getById(paymentMethodId);
        boolean fundsAvailable = false;

        if (optionalPaymentMethod.isEmpty()) {
            log.warn("Payment method not found - id={}", paymentMethodId);
            execution.setVariable("failureReason", "Payment method not found");
        } else {
            Payment paymentMethod = optionalPaymentMethod.get();
            execution.setVariable("resolvedPaymentType", paymentMethod.getType());
            fundsAvailable = paymentMethodService.hasSufficientFunds();

            if (!fundsAvailable) {
                log.warn("Insufficient funds - accountId={}, amount={}", accountId, amount);
                execution.setVariable("failureReason", "Insufficient funds");
            }
        }

        execution.setVariable("fundsAvailable", fundsAvailable);
    }
}
