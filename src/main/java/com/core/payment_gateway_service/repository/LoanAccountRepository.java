package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanAccountRepository extends JpaRepository<LoanAccount, String>{
    // Define any additional query methods if needed
}
