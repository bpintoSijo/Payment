package com.payments.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.cucumber.ScenarioContext;
import com.payments.repository.PaymentMethodRepository;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PaymentMethodSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private Map<String, Object> requestBody;

    @Given("I want to create a {string} payment method with account {string}")
    public void iWantToCreateAPaymentMethod(String type, String accountId) {
        requestBody = Map.of("type", type, "accountId", accountId, "id", 0);
    }

    @Given("a {string} payment method with account {string} exists")
    public void aPaymentMethodExists(String type, String accountId) throws Exception {
        Map<String, Object> body = Map.of("type", type, "accountId", accountId, "id", 0);
        MvcResult result = mockMvc.perform(post("/api/paymentMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        Map<?, ?> response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        scenarioContext.setLastCreatedPaymentMethodId(((Number) response.get("id")).longValue());
    }

    @When("I submit the payment method")
    public void iSubmitThePaymentMethod() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/paymentMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andReturn();
        scenarioContext.setLastResult(result);
    }

    @When("I request all available payment methods")
    public void iRequestAllAvailablePaymentMethods() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/paymentMethod")).andReturn();
        scenarioContext.setLastResult(result);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(scenarioContext.getLastResult().getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the payment method type should be {string}")
    public void thePaymentMethodTypeShouldBe(String expectedType) throws Exception {
        String body = scenarioContext.getLastResult().getResponse().getContentAsString();
        Map<?, ?> response = objectMapper.readValue(body, Map.class);
        assertThat(response.get("type")).isEqualTo(expectedType);
    }

    @Then("the payment method account should be {string}")
    public void thePaymentMethodAccountShouldBe(String expectedAccount) throws Exception {
        String body = scenarioContext.getLastResult().getResponse().getContentAsString();
        Map<?, ?> response = objectMapper.readValue(body, Map.class);
        assertThat(response.get("accountId")).isEqualTo(expectedAccount);
    }

    @Then("the response should be a JSON array")
    public void theResponseShouldBeAJsonArray() throws Exception {
        String body = scenarioContext.getLastResult().getResponse().getContentAsString();
        assertThat(body.trim()).startsWith("[");
    }

    @After
    public void afterScenario() {
        paymentMethodRepository.deleteAll();
    }
}
