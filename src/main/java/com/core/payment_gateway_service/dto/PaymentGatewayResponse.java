package com.core.payment_gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayResponse {
    private String paymentGatewayId;
    private String gatewayName;
    private String gatewayCode;
    private String description;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
