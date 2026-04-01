package com.payments.repository;

import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.payment.PaypalPayment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PaymentMethodRepositoryTest {

    @Autowired
    private PaymentMethodRepository repository;

    @Test
    void save_andFindById_returnsEntity() {
        CreditCardPayment saved = repository.save(new CreditCardPayment("card-001"));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting("accountId").isEqualTo("card-001");
    }

    @Test
    void findAll_returnsAllSavedPaymentMethods() {
        repository.save(new CreditCardPayment("card-001"));
        repository.save(new PaypalPayment("paypal@test.com"));
        repository.save(new CryptoPayment("0xABC"));

        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void findByPaymentMethodType_filtersByCreditCard() {
        repository.save(new CreditCardPayment("card-001"));
        repository.save(new CreditCardPayment("card-002"));
        repository.save(new PaypalPayment("paypal@test.com"));

        List<?> cards = repository.findByPaymentMethodType(CreditCardPayment.class);

        assertThat(cards).hasSize(2);
    }

    @Test
    void findByPaymentMethodType_filtersByPaypal() {
        repository.save(new CreditCardPayment("card-001"));
        repository.save(new PaypalPayment("paypal@test.com"));
        repository.save(new CryptoPayment("0xABC"));

        List<?> paypals = repository.findByPaymentMethodType(PaypalPayment.class);

        assertThat(paypals).hasSize(1);
    }

    @Test
    void findByPaymentMethodType_noMatch_returnsEmpty() {
        repository.save(new CreditCardPayment("card-001"));

        List<?> cryptos = repository.findByPaymentMethodType(CryptoPayment.class);

        assertThat(cryptos).isEmpty();
    }
}
