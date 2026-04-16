package com.payments.controller;

import com.payments.config.BaseSecurityTest;
import com.payments.config.TestConfig;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.service.PaymentMethodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class PaymentMethodControllerTest extends BaseSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    @Test
    @DisplayName("GET /payment-methods/new — return HTTP 200")
    void showForm_returns200() {
        List<PaymentMethodDTO> paymentMethodDTO = List.of(mock(PaymentMethodDTO.class), mock(PaymentMethodDTO.class));
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(paymentMethodDTO);

        ResponseEntity<String> response = restTemplate.getForEntity("/payment-methods/new", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET /payment-methods/new — Body's response is not empty")
    void showForm_responseBodyIsNotNull() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity("/payment-methods/new", String.class);

        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("GET /payment-methods/new — appelle getAvailablePaymentMethod() une seule fois")
    void showForm_callsGetAvailablePaymentMethod() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        restTemplate.getForEntity("/payment-methods/new", String.class);

        verify(paymentMethodService, times(1)).getAvailablePaymentMethod(anyLong());
    }

    @Test
    @DisplayName("GET /payment-methods/new — Works with a payment method list")
    void showForm_withPaymentMethods_returns200() {
        List<PaymentMethodDTO> paymentMethodDTO = List.of(mock(PaymentMethodDTO.class), mock(PaymentMethodDTO.class));
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(paymentMethodDTO);

        ResponseEntity<String> response = restTemplate.getForEntity("/payment-methods/new", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(paymentMethodService, times(1)).getAvailablePaymentMethod(anyLong());
    }

    @Test
    @DisplayName("GET /payment-methods/new — fonctionne si aucun moyen de paiement n'existe")
    void showForm_withNoPaymentMethods_returns200() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong())).thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity("/payment-methods/new", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /payment-methods/new — redirige vers /payment-methods/new après succès")
    void submitForm_redirectsAfterSuccess() {
        when(paymentMethodService.create(anyLong(), any())).thenReturn(mock(PaymentMethodDTO.class));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-001");
        ResponseEntity<String> response = restTemplate.postForEntity("/payment-methods/new", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /payment-methods/new — appelle paymentMethodService.create() une seule fois")
    void submitForm_callsServiceCreateOnce() {
        when(paymentMethodService.create(anyLong(), any())).thenReturn(mock(PaymentMethodDTO.class));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "PAYPAL");
        formData.add("accountId", "ACC-002");

        restTemplate.postForEntity("/payment-methods/new", formData, String.class);
        verify(paymentMethodService, times(1)).create(anyLong(), any());
    }

    @Test
    @DisplayName("POST /payment-methods/new — le message de succès contient le type et l'accountId")
    void submitForm_successMessageContainsTypeAndAccountId() {
        when(paymentMethodService.create(anyLong(), any())).thenReturn(mock(PaymentMethodDTO.class));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-123");

        ResponseEntity<String> response = restTemplate.postForEntity("/payment-methods/new", formData, String.class);
        assertThat(response.getBody()).contains("CARD", "ACC-123");
    }

    @Test
    @DisplayName("POST /payment-methods/new — retourne 500 si le service lève une exception")
    void submitForm_returns500_whenServiceThrows() {
        doThrow(new RuntimeException("Erreur service")).when(paymentMethodService).create(anyLong(), any());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-001");

        ResponseEntity<String> response = restTemplate.postForEntity("/payment-methods/new", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}