package com.core.payment_gateway_service.dto;

import com.core.payment_gateway_service.entity.EscrowAccount;
import com.core.payment_gateway_service.entity.PaymentGateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentGatewayTransactionRequest {
    private PaymentGateway paymentGateway;
    private String paymentGatewayConfigId;
    private String pgTransactionId;
    private String transactionType;
    private Integer amount;
    private String currency;
    private String status;
    private String description;
    private EscrowAccount targetEscrowAccountId;
    private String paymentMethodDetails;
    private String externalTransactionId;

}
