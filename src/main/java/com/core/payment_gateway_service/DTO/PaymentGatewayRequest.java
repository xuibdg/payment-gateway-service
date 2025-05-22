package com.core.payment_gateway_service.DTO;

import lombok.Data;

@Data
public class PaymentGatewayRequest {
    private String gatewayName;
    private String gatewayCode;
    private Boolean isActive;
    private String description;
}
