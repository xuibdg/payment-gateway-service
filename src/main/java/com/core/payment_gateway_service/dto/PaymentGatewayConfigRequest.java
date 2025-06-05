package com.core.payment_gateway_service.dto;

import lombok.Data;

@Data
public class PaymentGatewayConfigRequest {
    private String configName;
    private String configValue;
    private Boolean isSensitive;
    private Boolean isActive;
    private String description;
}
