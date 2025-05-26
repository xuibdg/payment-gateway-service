package com.core.payment_gateway_service.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FlipResponse {
    private String status;
    private String message;
    private String linkId;
    private String expiredDate;

    private BilPaymentEntity billPayment;
    private Customer customer;

    public FlipResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BilPaymentEntity {
        private String id;
        private int amount;
        private int unique_code;
        private String status;
        private String sender_bank;
        private String senderBankType;
        private ReceiverBankAccount receiverBankAccount;

        private String userAddress;
        private String userPhone;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Timestamp created_at;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ReceiverBankAccount {
        private String accountNumber;
        private String accountType;
        private String bankCode;
        private String accountHolder;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Customer {
        private String name;
        private String email;
        private String address;
        private String phone;
    }

}