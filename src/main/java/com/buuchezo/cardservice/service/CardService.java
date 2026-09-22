package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.dto.CardResponse;
import com.buuchezo.cardservice.dto.CreateCardRequest;
import com.buuchezo.cardservice.enums.CardStatus;

import java.util.List;

public interface CardService {

    CardResponse getCardById(Long id);

    CardResponse getCardByNumber(String cardNumber);

    List<CardResponse> getCardsByAccountNumber(String accountNumber);

    List<CardResponse> getCardsByAccountNumberAndStatus(
            String accountNumber,
            CardStatus cardStatus
    );

    List<CardResponse> getAllCards();

    CardResponse createCard(CreateCardRequest request);

    CardResponse activateCard(Long id);

    CardResponse blockCard(Long id);

    CardResponse cancelCard(Long id);
}
