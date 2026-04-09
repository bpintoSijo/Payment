package com.payments.service;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.Payment;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.strategy.payment.PaymentStrategyRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStrategyRegistry paymentStrategyRegistry;
    private final TransactionService transactionService;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository,
                                PaymentStrategyRegistry paymentStrategyRegistry,
                                TransactionService transactionService
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentStrategyRegistry = paymentStrategyRegistry;
        this.transactionService = transactionService;
    }

    @Transactional
    public PaymentMethodDTO create(PaymentMethodDTO paymentMethodDTO) {
        AbstractPaymentMethod payment = paymentStrategyRegistry.create(paymentMethodDTO);
        paymentMethodRepository.save(payment);
        return paymentStrategyRegistry.toDTO(payment);
    }

    @Transactional(readOnly = true)
    public Optional<AbstractPaymentMethod> getById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    public boolean hasSufficientFunds(Payment paymentMethod, BigDecimal amount) {
        // TODO fund management
        return true;
    }

    public boolean pay(AbstractPaymentMethod payment, BigDecimal amount) {
        boolean paymentSuccess = payment.pay(amount);
        transactionService.create(amount, payment.getId());
        return paymentSuccess;
    }

    @Transactional
    public List<PaymentMethodDTO> getAvailablePaymentMethod() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentStrategyRegistry::toDTO)
                .toList();
    }
}
