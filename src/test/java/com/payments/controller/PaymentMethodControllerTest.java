package com.payments.controller;

import com.payments.config.security.WithMockUserDetails;
import com.payments.service.PaymentMethodService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    // -------------------------------------------------------------------------
    // GET /payment-methods/new
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /payment-methods/new — return 200")
    @WithMockUserDetails
    void showForm_returns200() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/payment-methods/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUserDetails
    @DisplayName("GET /payment-methods/new — return 'paymentMethod/form' view")
    void showForm_returnsCorrectView() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/payment-methods/new"))
                .andExpect(view().name("paymentMethod/form"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("GET /payment-methods/new — model contain 'form' and 'paymentMethods'")
    void showForm_modelContainsExpectedAttributes() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/payment-methods/new"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("paymentMethods"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("GET /payment-methods/new — Call getAvailablePaymentMethod() once")
    void showForm_callsServiceOnce() throws Exception {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/payment-methods/new"));

        verify(paymentMethodService, times(1)).getAvailablePaymentMethod(1L);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /payment-methods/new — Unauthenticated return 401")
    void showForm_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/payment-methods/new"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // POST /payment-methods/new
    // -------------------------------------------------------------------------

    @Test
    @WithMockUserDetails
    @DisplayName("POST /payment-methods/new — redirect to /payment-methods/new")
    void submitForm_redirectsToForm() throws Exception {
        mockMvc.perform(post("/payment-methods/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", "CARD")
                        .param("accountId", "ACC-001")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment-methods/new"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("POST /payment-methods/new — call create() once")
    void submitForm_callsCreateOnce() throws Exception {
        mockMvc.perform(post("/payment-methods/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", "CARD")
                        .param("accountId", "ACC-001")
                )
                .andExpect(status().is3xxRedirection());

        verify(paymentMethodService, times(1)).create(eq(1L), any());
    }

    @Test
    @WithMockUserDetails
    @DisplayName("POST /payment-methods/new — flash message contain type and accountId")
    void submitForm_flashMessageContainsTypeAndAccountId() throws Exception {
        mockMvc.perform(post("/payment-methods/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", "CARD")
                        .param("accountId", "ACC-001")
                )
                .andExpect(flash().attribute("successMessage",
                        containsString("CARD")))
                .andExpect(flash().attribute("successMessage",
                        containsString("ACC-001")));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /payment-methods/new — Unauthenticated return 401")
    void submitForm_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/payment-methods/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", "CARD")
                        .param("accountId", "ACC-001")
                )
                .andExpect(status().isUnauthorized());
    }
}