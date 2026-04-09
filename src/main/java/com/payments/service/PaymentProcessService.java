package com.payments.service;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentProcessService {

    private final RuntimeService runtimeService;

    public PaymentProcessService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * Starts a payment transaction process.
     *
     * @param accountId       the account initiating the payment
     * @param paymentMethodId the ID of the AbstractPaymentMethod (CreditCardPayment, PaypalPayment, etc.)
     * @param paymentType     the type string returned by getType() — e.g. "CREDIT_CARD", "PAYPAL"
     * @param amount          the amount to pay
     * @return the Camunda process instance ID (useful for tracking / history queries)
     */
    public String startPaymentTransaction(String accountId,
                                          Long paymentMethodId,
                                          String paymentType,
                                          BigDecimal amount) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("accountId", accountId);
        variables.put("paymentMethodId", paymentMethodId);
        variables.put("paymentMethodType", paymentType);
        variables.put("amount", amount);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                "paymentTransactionProcess", variables);

        return instance.getId();
    }
}
