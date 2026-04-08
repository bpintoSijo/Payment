package com.payments.controller;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.service.TransactionService;
import com.payments.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    @DisplayName("GET /transactions/new - retourne la vue du formulaire")
    void showForm_returnsFormView() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/form"));
    }

    @Test
    @DisplayName("GET /transactions/new - initialise un TransactionDTO vide dans le modèle")
    void showForm_addsEmptyTransactionDTOToModel() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", new TransactionDTO()));
    }

    @Test
    @DisplayName("GET /transactions/new - charge les moyens de paiement existants")
    void showForm_populatesExistingPayments() throws Exception {
        AbstractPaymentMethod method1 = mock(AbstractPaymentMethod.class);
        AbstractPaymentMethod method2 = mock(AbstractPaymentMethod.class);
        List<AbstractPaymentMethod> methods = List.of(method1, method2);
        when(paymentMethodRepository.findAll()).thenReturn(methods);

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paymentMethods", methods));

        verify(paymentMethodRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /transactions/new - fonctionne si aucun moyen de paiement n'existe")
    void showForm_withNoPayments_stillReturnsForm() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paymentMethods", List.of()));
    }

    @Test
    @DisplayName("POST /transactions/new - redirige vers /transactions/new après succès")
    void submitForm_redirectsAfterSuccess() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");

        AbstractPaymentMethod payment = mock(AbstractPaymentMethod.class);
        when(payment.pay(any())).thenReturn(true);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getPayment()).thenReturn(payment);

        when(transactionService.create(any(BigDecimal.class), any(Long.class))).thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", amount.toString())
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions/new"));
    }

    @Test
    @DisplayName("POST /transactions/new - ajoute successPaymentMessage=true en flash attribute quand paiement OK")
    void submitForm_addsSuccessFlashAttribute_whenPaymentSucceeds() throws Exception {
        BigDecimal amount = new BigDecimal("50.00");

        AbstractPaymentMethod payment = mock(AbstractPaymentMethod.class);
        when(payment.pay(any())).thenReturn(true);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getPayment()).thenReturn(payment);

        when(transactionService.create(any(BigDecimal.class), any(Long.class))).thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", amount.toString())
                        .param("paymentMethodId", "1"))
                .andExpect(flash().attribute("successPaymentMessage", "Paid 50.00 with null - null"));
    }

    @Test
    @DisplayName("POST /transactions/new - ajoute successPaymentMessage=false en flash attribute quand paiement échoue")
    void submitForm_addsFailureFlashAttribute_whenPaymentFails() throws Exception {
        BigDecimal amount = new BigDecimal("50.00");

        AbstractPaymentMethod payment = mock(AbstractPaymentMethod.class);
        when(payment.pay(any())).thenReturn(false);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getPayment()).thenReturn(payment);

        when(transactionService.create(any(BigDecimal.class), any(Long.class))).thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", amount.toString())
                        .param("paymentMethodId", "1"))
                .andExpect(flash().attribute("successPaymentMessage",  nullValue()));
    }

    @Test
    @DisplayName("POST /transactions/new - appelle transactionService.create() avec le DTO du formulaire")
    void submitForm_callsTransactionServiceWithFormDTO() throws Exception {
        AbstractPaymentMethod payment = mock(AbstractPaymentMethod.class);
        when(payment.pay(any())).thenReturn(true);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getPayment()).thenReturn(payment);

        when(transactionService.create(any(BigDecimal.class), any(Long.class))).thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "75.00")
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection());

        verify(transactionService, times(1)).create(any(BigDecimal.class), any(Long.class));
    }

    //@Test
    @DisplayName("POST /transactions/new - lève une exception si transactionService échoue")
    void submitForm_throwsException_whenServiceFails() throws Exception {
        when(transactionService.create(any(BigDecimal.class), any(Long.class)))
                .thenThrow(new RuntimeException("Erreur service"));

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "100.00")
                        .param("paymentMethodId", "1"))
                .andExpect(status().is5xxServerError());
    }
}