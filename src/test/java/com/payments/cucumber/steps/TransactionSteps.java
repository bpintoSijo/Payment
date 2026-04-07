package com.payments.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cucumber.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TransactionSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScenarioContext scenarioContext;

    @When("I create a transaction with amount {double} for that payment method")
    public void iCreateATransactionForThatPaymentMethod(double amount) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", BigDecimal.valueOf(amount));
        body.put("paymentMethodId", scenarioContext.getLastCreatedPaymentMethodId());

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        scenarioContext.setLastResult(result);
    }

    @When("I create a transaction with amount {double} for payment method id {long}")
    public void iCreateATransactionForPaymentMethodId(double amount, long paymentMethodId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", BigDecimal.valueOf(amount));
        body.put("paymentMethodId", paymentMethodId);

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        scenarioContext.setLastResult(result);
    }

    @Then("the transaction amount should be {double}")
    public void theTransactionAmountShouldBe(double expectedAmount) throws Exception {
        String body = scenarioContext.getLastResult().getResponse().getContentAsString();
        Map<?, ?> response = objectMapper.readValue(body, Map.class);
        BigDecimal actual = new BigDecimal(response.get("amount").toString());
        assertThat(actual.compareTo(BigDecimal.valueOf(expectedAmount))).isZero();
    }
}
