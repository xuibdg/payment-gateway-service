package com.core.payment_gateway_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill_payment_data")
public class BillPayment {
    @Id
    private String id;
    private String title;
    private String type;
    private Integer amount;
    private String status;
    private String senderName;
    private String senderEmail;
    private String senderBank;
    private String senderBankType;


    private String paymentId;
    private Integer paymentAmount;
    private String paymentStatus;

    private String accountNumber;
    private String bankCode;
    private String accountHolder;

    private String customerPhone;
    private String customerAddress;
    private Timestamp createdAt;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

}
