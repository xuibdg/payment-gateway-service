package com.core.payment_gateway_service.repository;

import com.core.payment_gateway_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByEmailAndFullNameAndPhoneNumber(String email, String fullName, String phoneNumber);
    Optional<Customer> findByEmail(String senderEmail);
    Optional <Customer> findByFullName(String senderName);
}
