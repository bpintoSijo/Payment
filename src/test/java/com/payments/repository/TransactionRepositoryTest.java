package com.payments.repository;

import com.payments.domain.User;
import com.payments.domain.payment.CreditCardPayment;
import com.payments.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private User user;
    private CreditCardPayment payment;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("testuser")
                .email("test@test.com")
                .password("password")
                .build());

        CreditCardPayment card = new CreditCardPayment("card-001");
        card.setOwner(user);
        payment = paymentMethodRepository.save(card);
    }

    private Transaction buildTransaction(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setPayment(payment);
        transaction.setOwner(user);
        return transaction;
    }

    @Test
    void save_andFindById_returnsEntity() {
        Transaction saved = repository.save(buildTransaction(BigDecimal.TEN));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting("amount").isEqualTo(BigDecimal.TEN);
    }

    @Test
    void findAll_returnsAllSavedTransactions() {
        repository.save(buildTransaction(BigDecimal.TEN));
        repository.save(buildTransaction(BigDecimal.ONE));
        repository.save(buildTransaction(new BigDecimal("99.99")));

        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void deleteById_removesEntity() {
        Transaction saved = repository.save(buildTransaction(BigDecimal.TEN));
        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_notFound_returnsEmpty() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void save_transactionHasCorrectOwner() {
        Transaction saved = repository.save(buildTransaction(BigDecimal.TEN));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting(t -> ((Transaction) t).getOwner().getId())
                .isEqualTo(user.getId());
    }

    @Test
    void save_transactionHasCorrectPaymentMethod() {
        Transaction saved = repository.save(buildTransaction(BigDecimal.TEN));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting(t -> ((Transaction) t).getPayment().getId())
                .isEqualTo(payment.getId());
    }

    @Test
    void count_returnsCorrectCount() {
        repository.save(buildTransaction(BigDecimal.TEN));
        repository.save(buildTransaction(BigDecimal.ONE));
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void save_emptyRepository_countIsZero() {
        assertThat(repository.count()).isZero();
    }
}