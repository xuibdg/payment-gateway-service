package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGatewayCallback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayCallbackRepository extends JpaRepository<PaymentGatewayCallback, String> {
}
