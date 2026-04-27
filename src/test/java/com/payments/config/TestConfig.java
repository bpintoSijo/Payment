package com.payments.config;

import com.payments.service.PaymentProcessService;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableAutoConfiguration(exclude = {
        CamundaBpmAutoConfiguration.class
})
public class TestConfig {

    @Bean
    @Primary
    public PaymentProcessService paymentProcessService() {
        return mock(PaymentProcessService.class);
    }
}