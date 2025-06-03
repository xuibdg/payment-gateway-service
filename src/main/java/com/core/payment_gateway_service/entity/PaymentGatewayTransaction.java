package com.core.payment_gateway_service.entity;

import com.core.payment_gateway_service.enums.TransactionStatus;
import com.core.payment_gateway_service.enums.TransactionType;
import com.core.payment_gateway_service.service.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;
import java.util.Map;

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
    @JoinColumn(name = "payment_gateway_id")
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

    // Define these fields once saving/loan/escrow entities exist
    @ManyToOne
    @JoinColumn(name = "saving_account_id")
    private SavingAccount targetSavingAccountId;

    @ManyToOne
    @JoinColumn(name = "loan_account_id", nullable = true)
    private LoanAccount targetLoanAccountId;

    @ManyToOne
    @JoinColumn(name = "escrow_account_id", nullable = true)
    private EscrowAccount targetEscrowAccountId;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
