package com.buuchezo.cardservice.repository;

import com.buuchezo.cardservice.entity.Card;
import com.buuchezo.cardservice.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByAccountNumber(String accountNumber);

    List<Card> findByAccountNumberAndCardStatus(
            String accountNumber,
            CardStatus cardStatus
    );

    boolean existsByCardNumber(String cardNumber);
}
