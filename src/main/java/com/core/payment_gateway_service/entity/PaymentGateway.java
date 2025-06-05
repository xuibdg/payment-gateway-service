package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "payment_gateways")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGateway {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String paymentGatewayId;


    @OneToMany(mappedBy = "paymentGateway", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PaymentGatewayConfig> configs;

    @Column(nullable = false, unique = true, length = 100)
    private String gatewayName;

    @Column(nullable = false, unique = true, length = 20)
    private String gatewayCode;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
