package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.EscrowAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscrowAccountRepository extends JpaRepository<EscrowAccount, String> {
}
