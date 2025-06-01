package com.core.payment_gateway_service.entity;

import com.core.payment_gateway_service.enums.CallbackProcessingStatus;
import com.core.payment_gateway_service.service.JsonMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment_gateway_callbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayCallback {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "pg_callback_id", nullable = false, updatable = false)
    private String pgCallbackId;

    @ManyToOne
    @JoinColumn(name = "pg_transaction_id", nullable = true)
    private PaymentGatewayTransaction paymentGatewayTransaction;

    @Column(length = 255)
    private String externalTransactionId;

    @Column(columnDefinition = "longtext")
    private String rawPayload;

    @Column(columnDefinition = "longtext")
    private String headers;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallbackProcessingStatus processingStatus = CallbackProcessingStatus.RECEIVED;

    private Timestamp processedAt;

    private String processingNotes;

    @CreationTimestamp
    private Timestamp createdAt;
}
