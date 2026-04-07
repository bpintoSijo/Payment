package com.payments.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class ScenarioContext {

    private MvcResult lastResult;

    private Long lastCreatedPaymentMethodId;

    public MvcResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(MvcResult lastResult) {
        this.lastResult = lastResult;
    }

    public Long getLastCreatedPaymentMethodId() {
        return lastCreatedPaymentMethodId;
    }

    public void setLastCreatedPaymentMethodId(Long lastCreatedPaymentMethodId) {
        this.lastCreatedPaymentMethodId = lastCreatedPaymentMethodId;
    }
}
