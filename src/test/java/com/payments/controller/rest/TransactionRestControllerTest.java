package com.payments.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.config.TestConfig;
import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.config.security.BaseSecurityTest;
import com.payments.exceptions.transaction.NegativeAmountException;
import com.payments.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
class TransactionRestControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private Transaction buildTransaction(String amount, AbstractPaymentMethod paymentMethod) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setPayment(paymentMethod);
        return transaction;
    }

    private TransactionDTO buildRequest(String amount) {
        TransactionDTO request = new TransactionDTO();
        request.setAmount(new BigDecimal(amount));
        request.setPaymentMethodId(1L);
        return request;
    }

    // -------------------------------------------------------------------------
    // POST /api/transactions/
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/transactions — return 200 with created TransactionDTO")
    void create_validRequest_returns200WithTransactionDTO() throws Exception {
        AbstractPaymentMethod creditCard = new CreditCardPayment("card-001");
        creditCard.setId(1L);

        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("99.99", creditCard));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("99.99"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethodId").value(1L))
                .andExpect(jsonPath("$.amount").value(99.99));
    }

    @Test
    @DisplayName("POST /api/transactions — return Content-Type application/json")
    void create_returnsJsonContentType() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("50.00", new CreditCardPayment("card-001")));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("50.00"))))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/transactions — call transactionService.create() once")
    void create_callsServiceOnce() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("99.99", new CreditCardPayment("card-001")));

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("99.99"))));

        verify(transactionService, times(1)).create(anyLong(), any(BigDecimal.class), anyLong());
    }

    @Test
    @DisplayName("POST /api/transactions — without Content-Type return 415")
    void create_withoutContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /api/transactions — empty body return 400")
    void create_withEmptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    //@Test
    @DisplayName("POST /api/transactions — retourne 500 si le service lève une exception")
    void create_returns500_whenServiceThrows() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenThrow(new RuntimeException("Erreur service"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("99.99"))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/transactions — works with a zero amount")
    void create_withZeroAmount_returns200() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("0.00", new CreditCardPayment("card-001")));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("0.00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0.00));
    }

    @Test
    @DisplayName("POST /api/transactions — doesn't work with a negative amount")
    void create_withNegativeAmount_return403() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenThrow(new NegativeAmountException("Could not pay with negative amount"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("-1.00"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Could not pay with negative amount"));
    }

    @Test
    @DisplayName("POST /api/transactions — doesn't work with a negative amount")
    void create_unknownPaymentMethod_return404() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenThrow(new EntityNotFoundException("Payment method not found"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("1.00"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment method not found"));
    }
}