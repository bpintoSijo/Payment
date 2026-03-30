package com.payments.controller;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.payment.PaypalPayment;
import com.payments.dto.payment.CreditCardPaymentDTO;
import com.payments.dto.payment.CryptoPaymentDTO;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.dto.payment.PaypalPaymentDTO;
import com.payments.mapper.PaymentMethodMapper;
import com.payments.repository.PaymentMethodRepository;
import com.payments.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PaymentMethodDTO> create(@RequestBody PaymentMethodDTO dto) {
        PaymentMethodDTO payment = paymentMethodService.create(dto);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("")
    public ResponseEntity<List<PaymentMethodDTO>> getAvailablePaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getAvailablePaymentMethod());
    }
}
