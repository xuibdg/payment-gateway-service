package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, String> {
    // Define any additional query methods if needed
}
