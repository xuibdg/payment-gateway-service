package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, String> {
    Optional<PaymentGateway> findByGatewayCode(String gatewayCode);

}
