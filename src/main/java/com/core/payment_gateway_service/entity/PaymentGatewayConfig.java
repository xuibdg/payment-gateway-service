package com.core.payment_gateway_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    @ToString.Exclude
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
