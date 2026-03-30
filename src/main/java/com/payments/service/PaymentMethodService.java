package com.payments.service;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.factory.PaymentFactoryRegistry;
import com.payments.mapper.PaymentMethodMapper;
import com.payments.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentFactoryRegistry paymentMethodFactoryRegistry;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository,
                                PaymentMethodMapper paymentMethodMapper,
                                PaymentFactoryRegistry paymentMethodFactoryRegistry
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
        this.paymentMethodFactoryRegistry = paymentMethodFactoryRegistry;
    }

    public PaymentMethodDTO create(PaymentMethodDTO paymentMethodDTO) {
        AbstractPaymentMethod payment = paymentMethodFactoryRegistry.createFrom(paymentMethodDTO);
        paymentMethodRepository.save(payment);
        return paymentMethodDTO;
    }

    public List<PaymentMethodDTO> getAvailablePaymentMethod() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentMethodMapper::toDTO)
                .toList();
    }
}
