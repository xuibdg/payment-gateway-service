package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, String> {
    Optional<PaymentGatewayTransaction> findByExternalTransactionId(String paymentId);
}
