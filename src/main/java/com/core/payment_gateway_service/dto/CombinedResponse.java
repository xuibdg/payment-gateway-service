package com.core.payment_gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CombinedResponse {
    private FlipResponse paymentResponse;
    private String callbackResponse;
//    private String externalTransactionId;
//    private String paymentGatewayId;
//    private String pgTransactionId;
    private String status;
    private String message;
}
