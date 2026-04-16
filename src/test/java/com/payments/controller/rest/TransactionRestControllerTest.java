package com.payments.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.config.TestConfig;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.config.BaseSecurityTest;
import com.payments.service.TransactionService;
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

    private Transaction buildTransaction(String amount, String accountId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setPayment(new CreditCardPayment(accountId));
        return transaction;
    }

    private TransactionDTO buildRequest(String amount, long paymentMethodId) {
        TransactionDTO request = new TransactionDTO();
        request.setAmount(new BigDecimal(amount));
        request.setPaymentMethodId(paymentMethodId);
        return request;
    }

    @Test
    @DisplayName("POST /api/transactions — retourne 200 avec le TransactionDTO créé")
    void create_validRequest_returns200WithTransactionDTO() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("99.99", "card-001"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("99.99", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(99.99));
    }

    @Test
    @DisplayName("POST /api/transactions — retourne le Content-Type application/json")
    void create_returnsJsonContentType() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("50.00", "card-001"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("50.00", 1L))))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/transactions — appelle transactionService.create() une seule fois")
    void create_callsServiceOnce() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("99.99", "card-001"));

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("99.99", 1L))));

        verify(transactionService, times(1)).create(anyLong(), any(BigDecimal.class), anyLong());
    }

    @Test
    @DisplayName("POST /api/transactions — retourne le montant correct dans la réponse")
    void create_returnsCorrectAmount() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("250.00", "card-002"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("250.00", 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(250.00));
    }

    @Test
    @DisplayName("POST /api/transactions — sans Content-Type retourne 415")
    void create_withoutContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /api/transactions — corps vide retourne 400")
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
                        .content(objectMapper.writeValueAsString(buildRequest("99.99", 1L))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/transactions — fonctionne avec un montant à zéro")
    void create_withZeroAmount_returns200() throws Exception {
        when(transactionService.create(anyLong(), any(BigDecimal.class), anyLong()))
                .thenReturn(buildTransaction("0.00", "card-001"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("0.00", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0.00));
    }
}