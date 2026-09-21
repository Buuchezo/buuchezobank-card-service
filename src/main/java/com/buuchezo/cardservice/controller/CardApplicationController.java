package com.buuchezo.cardservice.controller;

import com.buuchezo.cardservice.dto.CardApplicationResponse;
import com.buuchezo.cardservice.dto.CreateCardApplicationRequest;
import com.buuchezo.cardservice.service.CardApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards/applications")
@RequiredArgsConstructor
public class CardApplicationController {

    private final CardApplicationService cardApplicationService;

    // =========================================================
    // CUSTOMER
    // =========================================================

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardApplicationResponse> createApplication(
            @Valid @RequestBody CreateCardApplicationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardApplicationService.createApplication(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(
                cardApplicationService.getMyApplications()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CardApplicationResponse> getMyApplication(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardApplicationService.getMyApplication(id)
        );
    }

    // =========================================================
    // ADMIN
    // =========================================================

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CardApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(
                cardApplicationService.getPendingApplications()
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CardApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(
                cardApplicationService.getAllApplications()
        );
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardApplicationResponse> approveApplication(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cardApplicationService.approveApplication(id)
        );
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardApplicationResponse> rejectApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String rejectionReason = request.get("rejectionReason");

        return ResponseEntity.ok(
                cardApplicationService.rejectApplication(
                        id,
                        rejectionReason
                )
        );
    }
}
