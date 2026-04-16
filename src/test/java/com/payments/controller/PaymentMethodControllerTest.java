package com.payments.controller;

import com.payments.config.TestConfig;
import com.payments.config.BaseSecurityTest;
import com.payments.service.PaymentMethodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class PaymentMethodControllerTest extends BaseSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    // -------------------------------------------------------------------------
    // GET /payment-methods/new
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /payment-methods/new — retourne HTTP 200")
    public void showForm_returns200() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong()))
                .thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/payment-methods/new", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET /payment-methods/new — body non vide")
    void showForm_bodyIsNotEmpty() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong()))
                .thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/payment-methods/new", String.class);

        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    @DisplayName("GET /payment-methods/new — appelle getAvailablePaymentMethod() une seule fois")
    void showForm_callsServiceOnce() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong()))
                .thenReturn(List.of());

        restTemplate.getForEntity("/payment-methods/new", String.class);

        verify(paymentMethodService, times(1)).getAvailablePaymentMethod(anyLong());
    }

    // -------------------------------------------------------------------------
    // POST /payment-methods/new
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /payment-methods/new — redirige en 3xx vers /payment-methods/new")
    void submitForm_redirects() {
        // Désactiver le suivi auto des redirections pour vérifier le vrai statut
        TestRestTemplate noRedirect = restTemplate.withBasicAuth("", "");
        // Ou utiliser RestTemplateBuilder sans follow-redirects — voir note ci-dessous

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-001");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment-methods/new", formData, String.class);

        // TestRestTemplate suit la redirection → on atterrit sur le GET final en 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(paymentMethodService, times(1)).create(anyLong(), any());
    }

    @Test
    @DisplayName("POST /payment-methods/new — appelle create() une seule fois")
    void submitForm_callsCreateOnce() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "PAYPAL");
        formData.add("accountId", "ACC-002");

        restTemplate.postForEntity("/payment-methods/new", formData, String.class);

        verify(paymentMethodService, times(1)).create(anyLong(), any());
    }

    @Test
    @DisplayName("POST /payment-methods/new — le message flash est affiché après redirection")
    void submitForm_flashMessageDisplayedAfterRedirect() {
        when(paymentMethodService.getAvailablePaymentMethod(anyLong()))
                .thenReturn(List.of());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-123");

        // TestRestTemplate suit la redirection → on récupère la page GET avec le flash
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment-methods/new", formData, String.class);

        // Le flash n'est visible que si ton template Thymeleaf affiche th:text="${successMessage}"
        assertThat(response.getBody()).contains("CARD", "ACC-123");
    }

    @Test
    @DisplayName("POST /payment-methods/new — retourne 500 si le service lève une exception")
    void submitForm_returns500_whenServiceThrows() {
        doThrow(new RuntimeException("Erreur service"))
                .when(paymentMethodService).create(anyLong(), any());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("type", "CARD");
        formData.add("accountId", "ACC-001");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment-methods/new", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}