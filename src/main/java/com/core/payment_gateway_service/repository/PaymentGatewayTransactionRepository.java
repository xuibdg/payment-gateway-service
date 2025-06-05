package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import com.core.payment_gateway_service.enums.TransactionStatus;

import java.util.List;

public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, String> {
    List<PaymentGatewayTransaction> findByStatus(TransactionStatus status);
}


