package com.buuchezo.cardservice.dto;

import com.buuchezo.cardservice.enums.CardStatus;
import com.buuchezo.cardservice.enums.CardType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CardResponse {

    private Long id;

    private String maskedCardNumber;

    private String accountNumber;

    private CardType cardType;

    private CardStatus cardStatus;

    private String holderName;

    private LocalDate expiryDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
