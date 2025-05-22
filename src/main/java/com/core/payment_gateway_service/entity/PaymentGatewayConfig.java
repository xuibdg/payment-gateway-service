package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "payment_gateway_configs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"payment_gateway_id", "config_name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pgConfigId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_gateway_id")
    private PaymentGateway paymentGateway;

    @Column(nullable = false, length = 100)
    private String configName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String configValue;

    @Builder.Default
    private Boolean isSensitive = false;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
