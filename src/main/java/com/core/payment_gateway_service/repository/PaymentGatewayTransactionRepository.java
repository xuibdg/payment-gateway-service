package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.BillPayment;
import com.core.payment_gateway_service.entity.PaymentGatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, String> {
    Optional<PaymentGatewayTransaction> findByExternalTransactionId(String paymentId);

    //and escrow_account_id = :escrowAccountId tambahkan sebelum 'order' ini jika ingin menggunakan findByEscrowAccountID(untuk method tidak langsung relase di escrow account)
    @Query(value = "SELECT pg_transaction_id from payment_gateway_transactions pgt where status = 'PENDING_INITIATION' order by created_at desc limit 1", nativeQuery = true)
    Optional<String> findLatestPendingTransactionByEscrowAccountId(String escrowAccountId);
}
