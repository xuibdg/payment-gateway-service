package com.core.payment_gateway_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CombinedRequest {
    private String title;
    private String type;
    private Integer amount;
    private Integer step;
    private String senderBank;
    private String senderBankType;
    private String senderName;
    private String senderEmail;
    private String customerPhone;
    private String customerAddress;
    private String callbackData;
    private String callbackToken;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;

    private PaymentGatewayTransactionRequest paymentGatewayTransactionRequest;

    private String paymentGatewayId;
    private String escrowAccountId;
}
