package com.payments.service;

import com.payments.domain.User;
import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.Payment;
import com.payments.dto.payment.PaymentMethodDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.UserRepository;
import com.payments.strategy.payment.PaymentStrategyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStrategyRegistry paymentStrategyRegistry;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @Transactional
    public PaymentMethodDTO create(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userRepository.getReferenceById(userId);
        AbstractPaymentMethod payment = paymentStrategyRegistry.create(paymentMethodDTO);
        payment.setOwner(user);
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
        transactionService.create(payment.getOwner().getId(), amount, payment.getId());
        return paymentSuccess;
    }

    @Transactional
    public List<PaymentMethodDTO> getAvailablePaymentMethod(Long userId) {
        return paymentMethodRepository.findByOwnerId(userId)
                .stream()
                .map(paymentStrategyRegistry::toDTO)
                .toList();
    }
}
