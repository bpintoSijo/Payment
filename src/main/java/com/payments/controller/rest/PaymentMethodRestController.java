package com.payments.controller.rest;


import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.security.UserDetailsImpl;
import com.payments.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentMethod")
public class PaymentMethodRestController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodRestController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentMethodDTO> create(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PaymentMethodDTO dto) {
        PaymentMethodDTO payment = paymentMethodService.create(userDetails.getId(), dto);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentMethodDTO>> getAvailablePaymentMethods(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(paymentMethodService.getAvailablePaymentMethod(userDetails.getId()));
    }
}
