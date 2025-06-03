package com.core.payment_gateway_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BillPaymentResponse {
    private String linkId;
    private String status;
    private String title;
    private String amount;
    private String expiredDate;
    private String step;

    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    private String senderName;
    private String senderBank;
}
