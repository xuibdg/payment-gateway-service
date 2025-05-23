package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, String> {
}
