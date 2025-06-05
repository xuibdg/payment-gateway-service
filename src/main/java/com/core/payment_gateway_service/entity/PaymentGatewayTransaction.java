package com.core.payment_gateway_service.entity;

import com.core.payment_gateway_service.enums.TransactionStatus;
import com.core.payment_gateway_service.enums.TransactionType;
import com.core.payment_gateway_service.service.JsonMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment_gateway_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayTransaction {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "pg_transaction_id", columnDefinition = "CHAR(36)")
    private String pgTransactionId;

    @ManyToOne
    @JoinColumn(name = "payment_gateway_id", nullable = false)
    private PaymentGateway paymentGatewayId;

    @Column(nullable = false, unique = true)
    private String internalReferenceId;

    @Column(unique = true)
    private String externalTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private Integer amount;

    @Column(length = 3)
    private String currencyCode = "IDR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING_INITIATION;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "longtext")
    private Map<String, Object> paymentMethodDetails;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "longtext")
    private Map<String, Object> requestPayload;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "longtext")
    private Map<String, Object> responsePayload;

    private String failureReason;

    private Timestamp initiatedAt;
    private Timestamp completedAt;
    private Timestamp expiresAt;

    @ManyToOne
    @JoinColumn(name = "escrow_account_id", nullable = false)
    private EscrowAccount escrowAccountId;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
