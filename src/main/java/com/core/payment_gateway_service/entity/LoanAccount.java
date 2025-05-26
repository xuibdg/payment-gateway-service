package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loan_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "loan_account_id")
    private String loanAccountId;
}