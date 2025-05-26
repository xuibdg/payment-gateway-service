package com.core.payment_gateway_service.DTO;

import com.core.payment_gateway_service.enums.SearchOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilPaymentSearch {
    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String id;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String title;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String type;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private Integer amount;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String status;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String senderName;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String senderEmail;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String senderBank;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String senderBankType;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String paymentId;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private Integer paymentAmount;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String paymentStatus;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String accountNumber;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String bankCode;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String accountHolder;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String customerPhone;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private String customerAddress;

    @SearchParameter(operator = SearchOperator.LIKE_IGNORE_CASE)
    private Timestamp createdAt;
}