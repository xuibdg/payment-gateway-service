package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Table(name = "payment_gateways")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGateway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentGatewayId;

    @Column(nullable = false, unique = true, length = 100)
    private String gatewayName;

    @Column(nullable = false, unique = true, length = 20)
    private String gatewayCode;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
