package com.payments.factory;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.payment.PaymentMethodDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentFactoryRegistry {
    private final Map<Class<?>, PaymentFactory<?>> factories;

    public PaymentFactoryRegistry(List<PaymentFactory<?>> allFactories) {
        this.factories = allFactories.stream()
                .collect(Collectors.toMap(PaymentFactory::getSupportedType, Function.identity()));
    }


    @SuppressWarnings("unchecked")
    public AbstractPaymentMethod createFrom(PaymentMethodDTO dto) {
        PaymentFactory<PaymentMethodDTO> factory =
                (PaymentFactory<PaymentMethodDTO>) factories.get(dto.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for: " + dto.getClass().getName());
        }
        return factory.create(dto);
    }
}
