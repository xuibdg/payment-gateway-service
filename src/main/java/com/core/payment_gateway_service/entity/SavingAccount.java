package com.core.payment_gateway_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saving_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "saving_account_id")
    private String savingAccountId;
}