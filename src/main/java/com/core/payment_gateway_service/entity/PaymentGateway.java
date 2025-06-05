package com.core.payment_gateway_service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
