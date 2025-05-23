package com.core.payment_gateway_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayConfigResponse {
    private String pgConfigId;
    private String configName;
    private String configValue; // bisa disensor nanti kalau `isSensitive` = true
    private Boolean isSensitive;
    private String description;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
