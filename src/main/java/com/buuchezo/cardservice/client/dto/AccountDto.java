package com.buuchezo.cardservice.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String accountType;
    private String accountStatus;
    private String ownerEmail;

    private String ownershipType;

    private Long businessId;

    private String businessName;

    private LocalDateTime createdAt;
}
