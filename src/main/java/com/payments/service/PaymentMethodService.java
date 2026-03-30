package com.payments.service;

import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.mapper.PaymentMethodMapper;
import com.payments.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    public PaymentMethodDTO create(PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodDTO;
    }

    public List<PaymentMethodDTO> getAvailablePaymentMethod() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentMethodMapper::toDTO)
                .toList();
    }
}
