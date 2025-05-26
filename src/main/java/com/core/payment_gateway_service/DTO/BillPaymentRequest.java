package com.core.payment_gateway_service.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillPaymentRequest {

    private String id;
    private String linkId;
    private String billLinkId;
    private String billLink;
    private String title;
    private String type;
    private Integer amount;
    private String redirectUrl;
    private Integer step;
    private String senderName;
    private String senderEmail;
    private String senderBank;
    private String senderBankType;
    private String status;
    private String isPhoneNumberRequired;
    private String customerPhone;
    private String customerAddress;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "YYYY-MM-DD HH:mm")
    private LocalDateTime expiredDate;

}