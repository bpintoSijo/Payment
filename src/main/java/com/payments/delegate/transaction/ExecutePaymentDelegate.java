package com.payments.delegate.transaction;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.service.PaymentMethodService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component("executePaymentDelegate")
public class ExecutePaymentDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ExecutePaymentDelegate.class);

    private final PaymentMethodService paymentMethodService;

    public ExecutePaymentDelegate(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long paymentMethodId = (Long) execution.getVariable("paymentMethodId");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");

        log.info("Executing payment - paymentMethodId={}, amount={}", paymentMethodId, amount);

        Optional<AbstractPaymentMethod> optionalPaymentMethod = paymentMethodService.getById(paymentMethodId);

        boolean paymentSuccess = false;

        try {
            if(optionalPaymentMethod.isEmpty()) {
                log.error("Payment method not found.");
                execution.setVariable("failureReason", "No payment method found for id: " + paymentMethodId);
                return;
            }
            AbstractPaymentMethod paymentMethod = optionalPaymentMethod.get();
            paymentSuccess = paymentMethodService.pay(paymentMethod, amount);

            log.info("Payment - type={}, accountId={}, amount={}",
                    paymentMethod.getType(), paymentMethod.getAccountId(), amount);

            String transactionRef = generateTransactionRef(paymentMethod, amount);
            execution.setVariable("transactionRef", transactionRef);

        } catch (Exception e) {
            log.error("Payment execution failed - paymentMethodId={}, reason={}", paymentMethodId, e.getMessage());
            execution.setVariable("failureReason", "Payment execution error: " + e.getMessage());
        }

        execution.setVariable("paymentSuccess", paymentSuccess);
    }

    private String generateTransactionRef(AbstractPaymentMethod method, BigDecimal amount) {
        return method.getType() + "-" + method.getAccountId() + "-" + amount + "-" + System.currentTimeMillis();
    }
}
