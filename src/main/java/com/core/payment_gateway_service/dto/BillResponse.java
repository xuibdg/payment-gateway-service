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
public class BillResponse {
    private String linkId;
    private String status;
    private String title;
    private String amount;
    private Timestamp expiredDate;
    private String step;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String senderName;
    private String senderBank;

}