package com.payments.repository;

import com.payments.domain.payment.AbstractPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<AbstractPaymentMethod, Long> {
    @Query("SELECT p FROM AbstractPaymentMethod p WHERE TYPE(p) = :type")
    List<AbstractPaymentMethod> findByPaymentMethodType(@Param("type") Class<? extends AbstractPaymentMethod> type);

    List<AbstractPaymentMethod> findByOwnerId(Long userId);

    Optional<AbstractPaymentMethod> findByIdAndOwnerId(long id, Long userId);
}
