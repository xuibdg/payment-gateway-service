package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, String> {

    @Query(value = "select payment_gateway_id  from payment_gateways pg limit 1", nativeQuery = true)
    Optional<String> findFirstPaymentGatewayId();
}