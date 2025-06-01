package com.core.payment_gateway_service.DTO;


import com.core.payment_gateway_service.entity.EscrowAccount;
import com.core.payment_gateway_service.entity.LoanAccount;
import com.core.payment_gateway_service.entity.PaymentGateway;
import com.core.payment_gateway_service.entity.SavingAccount;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
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
    @NotNull(message = "Amount tidak boleh kosong")
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;

    private PaymentGatewayTransactionRequest paymentGatewayTransactionRequest;

    private String paymentGatewayId;
    private String targetSavingAccountId;
    private String targetLoanAccountId;
    private String targetEscrowAccountId;
}