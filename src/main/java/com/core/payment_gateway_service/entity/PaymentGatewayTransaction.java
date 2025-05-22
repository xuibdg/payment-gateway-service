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
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID pgTransactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_gateway_id")
    private PaymentGateway paymentGateway;

    @Column(nullable = false, unique = true)
    private String internalReferenceId;

    @Column(unique = true)
    private String externalTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 3)
    private String currencyCode = "IDR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING_INITIATION;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> paymentMethodDetails;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> requestPayload;

    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> responsePayload;

    private String failureReason;

    private ZonedDateTime initiatedAt;
    private ZonedDateTime completedAt;
    private ZonedDateTime expiresAt;

    // Define these fields once saving/loan/escrow entities exist
    private Long targetSavingAccountId;
    private Long targetLoanAccountId;
    private Long targetEscrowAccountId;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
