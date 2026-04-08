package com.payments.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.service.PaymentMethodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentMethodRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    @Test
    @DisplayName("POST /api/paymentMethod — retourne 200 avec le DTO créé")
    void create_validDTO_returns200WithCreatedDTO() throws Exception {
        CreditCardPaymentDTO request = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPaymentDTO response = new CreditCardPaymentDTO(1, "CARD", "card-001");

        when(paymentMethodService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/paymentMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CARD"))
                .andExpect(jsonPath("$.accountId").value("card-001"));
    }

    @Test
    @DisplayName("POST /api/paymentMethod — appelle paymentMethodService.create() une seule fois")
    void create_callsServiceOnce() throws Exception {
        CreditCardPaymentDTO request = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPaymentDTO response = new CreditCardPaymentDTO(1, "CARD", "card-001");

        when(paymentMethodService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/paymentMethod")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        verify(paymentMethodService, times(1)).create(any());
    }

    @Test
    @DisplayName("POST /api/paymentMethod — retourne le Content-Type application/json")
    void create_returnsJsonContentType() throws Exception {
        CreditCardPaymentDTO request = new CreditCardPaymentDTO(0, "CARD", "card-001");
        CreditCardPaymentDTO response = new CreditCardPaymentDTO(1, "CARD", "card-001");

        when(paymentMethodService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/paymentMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    //@Test
    @DisplayName("POST /api/paymentMethod — retourne 500 si le service lève une exception")
    void create_returns500_whenServiceThrows() throws Exception {
        CreditCardPaymentDTO request = new CreditCardPaymentDTO(0, "CARD", "card-001");

        when(paymentMethodService.create(any())).thenThrow(new RuntimeException("Erreur service"));

        mockMvc.perform(post("/api/paymentMethod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/paymentMethod — sans Content-Type retourne 415")
    void create_withoutContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/paymentMethod")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("GET /api/paymentMethod — retourne 200 avec la liste des moyens de paiement")
    void getAvailablePaymentMethods_returns200WithList() throws Exception {
        List<PaymentMethodDTO> methods = List.of(
                new CreditCardPaymentDTO(1, "CARD", "card-001"),
                new CreditCardPaymentDTO(2, "CARD", "card-002")
        );

        when(paymentMethodService.getAvailablePaymentMethod()).thenReturn(methods);

        mockMvc.perform(get("/api/paymentMethod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountId").value("card-001"))
                .andExpect(jsonPath("$[1].accountId").value("card-002"));
    }

    @Test
    @DisplayName("GET /api/paymentMethod — retourne 200 avec une liste vide")
    void getAvailablePaymentMethods_emptyList_returns200WithEmptyList() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod()).thenReturn(List.of());

        mockMvc.perform(get("/api/paymentMethod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/paymentMethod — retourne le Content-Type application/json")
    void getAvailablePaymentMethods_returnsJsonContentType() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod()).thenReturn(List.of());

        mockMvc.perform(get("/api/paymentMethod"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/paymentMethod — appelle getAvailablePaymentMethod() une seule fois")
    void getAvailablePaymentMethods_callsServiceOnce() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod()).thenReturn(List.of());

        mockMvc.perform(get("/api/paymentMethod"));

        verify(paymentMethodService, times(1)).getAvailablePaymentMethod();
    }

    //@Test
    @DisplayName("GET /api/paymentMethod — retourne 500 si le service lève une exception")
    void getAvailablePaymentMethods_returns500_whenServiceThrows() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod())
                .thenThrow(new RuntimeException("Erreur service"));

        mockMvc.perform(get("/api/paymentMethod"))
                .andExpect(status().isInternalServerError());
    }
}