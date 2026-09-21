package com.buuchezo.cardservice.controller;

import com.buuchezo.cardservice.dto.CardResponse;
import com.buuchezo.cardservice.dto.CreateCardRequest;
import com.buuchezo.cardservice.enums.CardStatus;
import com.buuchezo.cardservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.createCard(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardResponse> getCardById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardService.getCardById(id)
        );
    }

    @GetMapping("/number/{cardNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardResponse> getCardByNumber(
            @PathVariable String cardNumber
    ) {
        return ResponseEntity.ok(
                cardService.getCardByNumber(cardNumber)
        );
    }

    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardResponse>> getCardsByAccount(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(
                cardService.getCardsByAccountNumber(accountNumber)
        );
    }

    @GetMapping("/account/{accountNumber}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardResponse>> getCardsByAccountAndStatus(
            @PathVariable String accountNumber,
            @PathVariable CardStatus status
    ) {
        return ResponseEntity.ok(
                cardService.getCardsByAccountNumberAndStatus(
                        accountNumber,
                        status
                )
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardResponse> activateCard(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardService.activateCard(id)
        );
    }

    @PatchMapping("/{id}/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardResponse> blockCard(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardService.blockCard(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardResponse> cancelCard(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardService.cancelCard(id)
        );
    }
}
