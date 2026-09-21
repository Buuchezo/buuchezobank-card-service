package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.dto.CardResponse;
import com.buuchezo.cardservice.dto.CreateCardRequest;
import com.buuchezo.cardservice.entity.Card;
import com.buuchezo.cardservice.enums.CardStatus;
import com.buuchezo.cardservice.exception.CardAlreadyExistsException;
import com.buuchezo.cardservice.exception.CardNotFoundException;
import com.buuchezo.cardservice.exception.InvalidCardStateException;
import com.buuchezo.cardservice.repository.CardRepository;
import com.buuchezo.cardservice.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;

    @Override
    public CardResponse getCardById(Long id) {
        Card card = findCardById(id);
        card = updateExpiredStatusIfNecessary(card);

        return toResponse(card);
    }

    @Override
    public CardResponse getCardByNumber(String cardNumber) {

        String normalizedCardNumber =
                normalizeCardNumber(cardNumber);

        Card card = cardRepository
                .findByCardNumber(normalizedCardNumber)
                .orElseThrow(() ->
                        new CardNotFoundException(
                                "Card not found with number: "
                                        + normalizedCardNumber
                        )
                );

        card = updateExpiredStatusIfNecessary(card);

        return toResponse(card);
    }

    @Override
    public List<CardResponse> getCardsByAccountNumber(
            String accountNumber
    ) {
        return cardRepository
                .findByAccountNumber(accountNumber)
                .stream()
                .map(this::updateExpiredStatusIfNecessary)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CardResponse> getCardsByAccountNumberAndStatus(
            String accountNumber,
            CardStatus cardStatus
    ) {
        return cardRepository
                .findByAccountNumberAndCardStatus(
                        accountNumber,
                        cardStatus
                )
                .stream()
                .map(this::updateExpiredStatusIfNecessary)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CardResponse createCard(CreateCardRequest request) {

        validateExpiryDate(request.getExpiryDate());

        String cardNumber = generateUniqueCardNumber();

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .accountNumber(request.getAccountNumber())
                .cardType(request.getCardType())
                .cardStatus(CardStatus.ACTIVE)
                .holderName(request.getHolderName())
                .expiryDate(request.getExpiryDate())
                .build();

        Card savedCard = cardRepository.save(card);

        return toResponse(savedCard);
    }

    @Override
    public CardResponse activateCard(Long id) {

        Card card = findCardById(id);
        card = updateExpiredStatusIfNecessary(card);

        if (card.getCardStatus() == CardStatus.CANCELLED) {
            throw new InvalidCardStateException(
                    "Cancelled cards cannot be activated"
            );
        }

        if (card.getCardStatus() == CardStatus.EXPIRED) {
            throw new InvalidCardStateException(
                    "Expired cards cannot be activated"
            );
        }

        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardStateException(
                    "Card is already active"
            );
        }

        card.setCardStatus(CardStatus.ACTIVE);

        return toResponse(cardRepository.save(card));
    }

    @Override
    public CardResponse blockCard(Long id) {

        Card card = findCardById(id);
        card = updateExpiredStatusIfNecessary(card);

        if (card.getCardStatus() == CardStatus.CANCELLED) {
            throw new InvalidCardStateException(
                    "Cancelled cards cannot be blocked"
            );
        }

        if (card.getCardStatus() == CardStatus.EXPIRED) {
            throw new InvalidCardStateException(
                    "Expired cards cannot be blocked"
            );
        }

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardStateException(
                    "Card is already blocked"
            );
        }

        card.setCardStatus(CardStatus.BLOCKED);

        return toResponse(cardRepository.save(card));
    }

    @Override
    public CardResponse cancelCard(Long id) {

        Card card = findCardById(id);

        if (card.getCardStatus() == CardStatus.CANCELLED) {
            throw new InvalidCardStateException(
                    "Card is already cancelled"
            );
        }

        card.setCardStatus(CardStatus.CANCELLED);

        return toResponse(cardRepository.save(card));
    }

    private Card findCardById(Long id) {

        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new CardNotFoundException(
                                "Card not found with id: " + id
                        )
                );
    }

    private String generateUniqueCardNumber() {

        for (int attempt = 0; attempt < 10; attempt++) {

            String cardNumber =
                    cardNumberGenerator.generateCardNumber();

            if (!cardRepository.existsByCardNumber(cardNumber)) {
                return cardNumber;
            }
        }

        throw new CardAlreadyExistsException(
                "Unable to generate a unique card number"
        );
    }

    private String normalizeCardNumber(String cardNumber) {

        if (cardNumber == null) {
            return null;
        }

        return cardNumber.replaceAll("\\s+", "");
    }

    private void validateExpiryDate(LocalDate expiryDate) {

        if (expiryDate == null ||
                expiryDate.isBefore(LocalDate.now())) {

            throw new InvalidCardStateException(
                    "Card expiry date must be in the future"
            );
        }
    }

    private boolean isExpired(LocalDate expiryDate) {

        return expiryDate == null ||
                expiryDate.isBefore(LocalDate.now());
    }

    private Card updateExpiredStatusIfNecessary(Card card) {

        if (card.getCardStatus() != CardStatus.CANCELLED &&
                isExpired(card.getExpiryDate()) &&
                card.getCardStatus() != CardStatus.EXPIRED) {

            card.setCardStatus(CardStatus.EXPIRED);

            return cardRepository.save(card);
        }

        return card;
    }

    private CardResponse toResponse(Card card) {

        return CardResponse.builder()
                .id(card.getId())
                .maskedCardNumber(
                        maskCardNumber(card.getCardNumber())
                )
                .accountNumber(card.getAccountNumber())
                .cardType(card.getCardType())
                .cardStatus(card.getCardStatus())
                .holderName(card.getHolderName())
                .expiryDate(card.getExpiryDate())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    private String maskCardNumber(String cardNumber) {

        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        String lastFourDigits =
                cardNumber.substring(cardNumber.length() - 4);

        return "**** **** **** " + lastFourDigits;
    }
}
