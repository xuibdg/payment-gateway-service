package com.core.payment_gateway_service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FlipCallbackRequest {
    private String id;
    private String billLinkId;
    private String billLink;
    private String billTitle;
    private String senderName;
    private String senderEmail;
    private String senderBank;
    private String senderBankType;
    private Integer amount;
    private String status;
    private String externalTransactionId;
    private String paymentGatewayId;
    private String pgTransactionId;
}
