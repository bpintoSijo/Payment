package com.payments.service;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.strategy.payment.PaymentStrategyRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStrategyRegistry paymentStrategyRegistry;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository,
                                PaymentStrategyRegistry paymentStrategyRegistry
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentStrategyRegistry = paymentStrategyRegistry;
    }

    @Transactional
    public PaymentMethodDTO create(PaymentMethodDTO paymentMethodDTO) {
        AbstractPaymentMethod payment = paymentStrategyRegistry.create(paymentMethodDTO);
        paymentMethodRepository.save(payment);
        return paymentStrategyRegistry.toDTO(payment);
    }

    @Transactional
    public List<PaymentMethodDTO> getAvailablePaymentMethod() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentStrategyRegistry::toDTO)
                .toList();
    }
}
