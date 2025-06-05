package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, String>, JpaSpecificationExecutor<BillPayment> {
    Optional<BillPayment> findByPaymentId(String paymentId);

}