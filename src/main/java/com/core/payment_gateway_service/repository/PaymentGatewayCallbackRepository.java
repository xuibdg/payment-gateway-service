package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGatewayCallback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayCallbackRepository extends JpaRepository<PaymentGatewayCallback, UUID> {
}
