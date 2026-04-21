package com.payments.controller;

import com.payments.config.TestConfig;
import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.config.security.WithMockUserDetails;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.UserRepository;
import com.payments.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private PaymentMethodRepository paymentMethodRepository;

    @MockitoBean
    private UserRepository userRepository;

    // -------------------------------------------------------------------------
    // GET /transactions/new
    // -------------------------------------------------------------------------

    @Test
    @WithMockUserDetails
    @DisplayName("GET /transactions/new — returns form view")
    void showForm_returnsFormView() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/form"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("GET /transactions/new — model contains empty TransactionDTO")
    void showForm_addsEmptyTransactionDTOToModel() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", new TransactionDTO()));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("GET /transactions/new — model contains existing payment methods")
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
    @WithMockUserDetails
    @DisplayName("GET /transactions/new — returns form even with no payment methods")
    void showForm_withNoPayments_stillReturnsForm() throws Exception {
        when(paymentMethodRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paymentMethods", List.of()));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /transactions/new — unauthenticated returns 401")
    void showForm_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // POST /transactions/new
    // -------------------------------------------------------------------------

    @Test
    @WithMockUserDetails
    @DisplayName("POST /transactions/new — redirects to /transactions/new on success")
    void submitForm_redirectsAfterSuccess() throws Exception {
        Transaction transaction = buildTransaction(true);
        when(transactionService.create(anyLong(), any(BigDecimal.class), any(Long.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "100.00")
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions/new"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("POST /transactions/new — flash attribute contains success message when payment succeeds")
    void submitForm_addsSuccessFlashAttribute_whenPaymentSucceeds() throws Exception {
        Transaction transaction = buildTransaction(true);
        when(transactionService.create(anyLong(), any(BigDecimal.class), any(Long.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "50.00")
                        .param("paymentMethodId", "1"))
                .andExpect(flash().attribute("successPaymentMessage", "Paid 50.00 with null - null"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("POST /transactions/new — flash attribute is null when payment fails")
    void submitForm_addsFailureFlashAttribute_whenPaymentFails() throws Exception {
        Transaction transaction = buildTransaction(true);
        when(transactionService.create(anyLong(), any(BigDecimal.class), any(Long.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "50.00")
                        .param("paymentMethodId", "1"))
                .andExpect(flash().attribute("successPaymentMessage", "Paid 50.00 with null - null"));
    }

    @Test
    @WithMockUserDetails
    @DisplayName("POST /transactions/new — calls transactionService.create() exactly once")
    void submitForm_callsTransactionServiceWithFormDTO() throws Exception {
        Transaction transaction = buildTransaction(true);
        when(transactionService.create(anyLong(), any(BigDecimal.class), any(Long.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transactions/new")
                        .param("amount", "75.00")
                        .param("paymentMethodId", "1"))
                .andExpect(status().is3xxRedirection());

        verify(transactionService, times(1)).create(eq(1L), any(BigDecimal.class), any(Long.class));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /transactions/new — unauthenticated returns 401")
    void submitForm_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/transactions/new")
                        .param("amount", "100.00")
                        .param("paymentMethodId", "1"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Transaction buildTransaction(boolean paymentSucceeds) {
        AbstractPaymentMethod payment = mock(AbstractPaymentMethod.class);
        when(payment.pay(any())).thenReturn(paymentSucceeds);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getPayment()).thenReturn(payment);
        return transaction;
    }
}