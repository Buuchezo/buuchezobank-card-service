package com.buuchezo.cardservice.dto;

import com.buuchezo.cardservice.enums.CardApplicationStatus;
import com.buuchezo.cardservice.enums.CardType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CardApplicationResponse {

    private Long id;
    private String applicantEmail;
    private String accountNumber;
    private CardType cardType;
    private String holderName;
    private CardApplicationStatus applicationStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;

    /*
     * Populated after approval.
     * This allows the frontend to know which card
     * was created from the application.
     */
    private Long cardId;
}
