package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id")
    private String id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 255)
    private String email;
}
