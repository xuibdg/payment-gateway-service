package com.core.payment_gateway_service.entity;

import com.core.payment_gateway_service.enums.CallbackProcessingStatus;
import com.core.payment_gateway_service.service.JsonMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Table(name = "payment_gateway_callbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayCallback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pgCallbackId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_gateway_id")
    private PaymentGateway paymentGateway;

    @ManyToOne
    @JoinColumn(name = "pg_transaction_id")
    private PaymentGatewayTransaction paymentGatewayTransaction;

    @Column(length = 255)
    private String externalTransactionId;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> rawPayload;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> headers;

    @Column(nullable = false)
    @CreationTimestamp
    private ZonedDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallbackProcessingStatus processingStatus = CallbackProcessingStatus.RECEIVED;

    private ZonedDateTime processedAt;

    private String processingNotes;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}
