package com.payments.repository;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.payment.CryptoPayment;
import com.payments.domain.payment.PaypalPayment;
import com.payments.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PaymentMethodRepositoryTest {

    @Autowired
    private PaymentMethodRepository repository;

    @Autowired
    private UserRepository userRepository;

    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = userRepository.save(
                User.builder().username("John").email("John@test.com").password("password")
                .build()
        );
    }

    @Test
    void save_andFindById_returnsEntity() {
        CreditCardPayment saved = repository.save(new CreditCardPayment("card-001"));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting("accountId").isEqualTo("card-001");
    }

    @Test
    void findAll_returnsAllSavedPaymentMethods() {
        repository.save(createPayment("CARD", "card-001"));
        repository.save(createPayment("PAYPAL", "paypal@test.com"));
        repository.save(createPayment("CRYPTO", "0xABC"));

        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void findByPaymentMethodType_filtersByCreditCard() {
        repository.save(createPayment("CARD", "card-001"));
        repository.save(createPayment("CARD", "card-002"));
        repository.save(createPayment("PAYPAL", "paypal@test.com"));

        List<?> cards = repository.findByPaymentMethodType(CreditCardPayment.class);

        assertThat(cards).hasSize(2);
    }

    @Test
    void findByPaymentMethodType_filtersByPaypal() {
        repository.save(createPayment("CARD", "card-001"));
        repository.save(createPayment("PAYPAL", "paypal@test.com"));
        repository.save(createPayment("CRYPTO", "0xABC"));

        List<?> paypals = repository.findByPaymentMethodType(PaypalPayment.class);

        assertThat(paypals).hasSize(1);
    }

    @Test
    void findByPaymentMethodType_noMatch_returnsEmpty() {
        repository.save(createPayment("CARD", "card-001"));

        List<?> cryptos = repository.findByPaymentMethodType(CryptoPayment.class);

        assertThat(cryptos).isEmpty();
    }

    @Test
    void findByPaymentMethodType_filtersByCrypto() {
        repository.save(createPayment("CRYPTO", "0xABC"));
        repository.save(createPayment("CRYPTO", "0xDEF"));
        repository.save(createPayment("PAYPAL", "paypal@test.com"));

        List<?> cryptos = repository.findByPaymentMethodType(CryptoPayment.class);

        assertThat(cryptos).hasSize(2);
    }

    @Test
    void findByPaymentMethodType_emptyRepository_returnsEmpty() {
        assertThat(repository.findByPaymentMethodType(CreditCardPayment.class)).isEmpty();
    }

    @Test
    void deleteById_removesEntity() {
        CreditCardPayment saved = (CreditCardPayment) repository.save(createPayment("CARD", "card-001"));
        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findByOwnerId_returnsPaymentsForUser() {
        User user = defaultUser;
        User other = userRepository.save(User.builder()
                .username("otheruser")
                .email("other@test.com")
                .password("password")
                .build());

        CreditCardPayment card = new CreditCardPayment("card-001");
        card.setOwner(user);
        PaypalPayment paypal = new PaypalPayment("paypal@test.com");
        paypal.setOwner(user);
        CryptoPayment crypto = new CryptoPayment("0xABC");
        crypto.setOwner(other);
        repository.save(card);
        repository.save(paypal);
        repository.save(crypto);

        List<?> results = repository.findByOwnerId(user.getId());

        assertThat(results).hasSize(2);
    }

    @Test
    void findByOwnerId_noMatch_returnsEmpty() {
        assertThat(repository.findByOwnerId(999L)).isEmpty();
    }

    private AbstractPaymentMethod createPayment(String type, String accountId) {
        switch(type) {
            case "CARD" -> {
                var payment = new CreditCardPayment(accountId);
                payment.setOwner(defaultUser);
                return payment;
            }
            case "PAYPAL" -> {
                var payment = new PaypalPayment(accountId);
                payment.setOwner(defaultUser);
                return payment;
            }
            case "CRYPTO" -> {
                var payment = new CryptoPayment(accountId);
                payment.setOwner(defaultUser);
                return payment;
            }
            default -> throw new UnsupportedOperationException("Unknown payment type.");
        }
    }
}