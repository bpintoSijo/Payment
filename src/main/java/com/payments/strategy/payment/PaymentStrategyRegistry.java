package com.payments.strategy.payment;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.payment.PaymentMethodDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyRegistry {

    private final Map<Class<?>, PaymentStrategy<?, ?>> byDtoType;
    private final Map<Class<?>, PaymentStrategy<?, ?>> byPaymentType;

    public PaymentStrategyRegistry(List<PaymentStrategy<?, ?>> strategies) {
        this.byDtoType = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getSupportedDtoType, Function.identity()));
        this.byPaymentType = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getSupportedPaymentType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public AbstractPaymentMethod create(PaymentMethodDTO dto) {
        return ((PaymentStrategy<PaymentMethodDTO, AbstractPaymentMethod>)
                byDtoType.get(dto.getClass())).createFromDTO(dto);
    }

    @SuppressWarnings("unchecked")
    public void update(AbstractPaymentMethod payment, PaymentMethodDTO dto) {
        ((PaymentStrategy<PaymentMethodDTO, AbstractPaymentMethod>)
                byDtoType.get(dto.getClass())).update(payment, dto);
    }

    @SuppressWarnings("unchecked")
    public PaymentMethodDTO toDTO(AbstractPaymentMethod payment) {
        return ((PaymentStrategy<PaymentMethodDTO, AbstractPaymentMethod>)
                byPaymentType.get(payment.getClass())).toDTO(payment);
    }
}