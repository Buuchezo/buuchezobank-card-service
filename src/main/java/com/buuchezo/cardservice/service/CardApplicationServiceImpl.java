package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.dto.CardApplicationResponse;
import com.buuchezo.cardservice.dto.CardResponse;
import com.buuchezo.cardservice.dto.CreateCardApplicationRequest;
import com.buuchezo.cardservice.entity.CardApplication;
import com.buuchezo.cardservice.enums.CardApplicationStatus;
import com.buuchezo.cardservice.exception.CardApplicationException;
import com.buuchezo.cardservice.exception.CardNotFoundException;
import com.buuchezo.cardservice.repository.CardApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardApplicationServiceImpl implements CardApplicationService {

    private final CardApplicationRepository cardApplicationRepository;
    private final CardService cardService;
    private final AccountOwnershipService accountOwnershipService;

    @Override
    @Transactional
    public CardApplicationResponse createApplication(
            CreateCardApplicationRequest request
    ) {

        String applicantEmail = getAuthenticatedEmail();

        /*
         * This verifies that the authenticated customer actually owns
         * the account for which the card is being requested.
         */
        accountOwnershipService.verifyAccountAccess(
                request.getAccountNumber()
        );

        /*
         * Prevent multiple pending applications for the same
         * account/customer combination.
         */
        boolean pendingApplicationExists =
                cardApplicationRepository
                        .existsByAccountNumberAndApplicantEmailAndApplicationStatus(
                                request.getAccountNumber(),
                                applicantEmail,
                                CardApplicationStatus.PENDING
                        );

        if (pendingApplicationExists) {
            throw new CardApplicationException(
                    "You already have a pending card application for this account"
            );
        }

        CardApplication application = CardApplication.builder()
                .applicantEmail(applicantEmail)
                .accountNumber(request.getAccountNumber())
                .cardType(request.getCardType())
                .holderName(request.getHolderName())
                .applicationStatus(CardApplicationStatus.PENDING)
                .build();

        CardApplication savedApplication =
                cardApplicationRepository.save(application);

        return toResponse(savedApplication, null);
    }

    @Override
    public List<CardApplicationResponse> getMyApplications() {

        String applicantEmail = getAuthenticatedEmail();

        return cardApplicationRepository
                .findByApplicantEmailOrderByCreatedAtDesc(applicantEmail)
                .stream()
                .map(application -> toResponse(application, null))
                .toList();
    }

    @Override
    public CardApplicationResponse getMyApplication(Long id) {

        String applicantEmail = getAuthenticatedEmail();

        CardApplication application =
                cardApplicationRepository
                        .findByIdAndApplicantEmail(id, applicantEmail)
                        .orElseThrow(() ->
                                new CardNotFoundException(
                                        "Card application not found"
                                )
                        );

        return toResponse(application, null);
    }

    @Override
    public List<CardApplicationResponse> getPendingApplications() {

        requireAdmin();

        return cardApplicationRepository
                .findByApplicationStatusOrderByCreatedAtAsc(
                        CardApplicationStatus.PENDING
                )
                .stream()
                .map(application -> toResponse(application, null))
                .toList();
    }

    @Override
    public List<CardApplicationResponse> getAllApplications() {

        requireAdmin();

        return cardApplicationRepository
                .findAll()
                .stream()
                .map(application -> toResponse(application, null))
                .toList();
    }

    @Override
    @Transactional
    public CardApplicationResponse approveApplication(Long id) {

        requireAdmin();

        CardApplication application =
                findApplication(id);

        if (application.getApplicationStatus()
                != CardApplicationStatus.PENDING) {

            throw new CardApplicationException(
                    "Only pending applications can be approved"
            );
        }

        /*
         * Cards are valid until the end of the following year.
         * Example:
         * approval in September 2026 -> expiry December 31, 2027.
         */
        LocalDate expiryDate =
                LocalDate.of(
                        LocalDate.now().getYear() + 1,
                        12,
                        31
                );

        /*
         * Reuse the existing CardService so card-number generation,
         * uniqueness checks and card creation remain centralized.
         */
        com.buuchezo.cardservice.dto.CreateCardRequest cardRequest =
                new com.buuchezo.cardservice.dto.CreateCardRequest();

        cardRequest.setAccountNumber(application.getAccountNumber());
        cardRequest.setCardType(application.getCardType());
        cardRequest.setHolderName(application.getHolderName());
        cardRequest.setExpiryDate(expiryDate);

        CardResponse createdCard =
                cardService.createCard(cardRequest);

        application.setApplicationStatus(
                CardApplicationStatus.APPROVED
        );
        application.setReviewedAt(
                java.time.LocalDateTime.now()
        );
        application.setRejectionReason(null);

        CardApplication savedApplication =
                cardApplicationRepository.save(application);

        return toResponse(savedApplication, createdCard.getId());
    }

    @Override
    @Transactional
    public CardApplicationResponse rejectApplication(
            Long id,
            String rejectionReason
    ) {

        requireAdmin();

        CardApplication application =
                findApplication(id);

        if (application.getApplicationStatus()
                != CardApplicationStatus.PENDING) {

            throw new CardApplicationException(
                    "Only pending applications can be rejected"
            );
        }

        if (rejectionReason == null ||
                rejectionReason.trim().isEmpty()) {

            throw new CardApplicationException(
                    "A rejection reason is required"
            );
        }

        String normalizedReason =
                rejectionReason.trim();

        if (normalizedReason.length() > 500) {
            throw new CardApplicationException(
                    "Rejection reason must not exceed 500 characters"
            );
        }

        application.setApplicationStatus(
                CardApplicationStatus.REJECTED
        );
        application.setRejectionReason(normalizedReason);
        application.setReviewedAt(
                java.time.LocalDateTime.now()
        );

        CardApplication savedApplication =
                cardApplicationRepository.save(application);

        return toResponse(savedApplication, null);
    }

    private CardApplication findApplication(Long id) {

        return cardApplicationRepository
                .findById(id)
                .orElseThrow(() ->
                        new CardNotFoundException(
                                "Card application not found with id: " + id
                        )
                );
    }

    private String getAuthenticatedEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        return authentication.getName();
    }

    private void requireAdmin() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                "ADMIN".equals(
                                        authority.getAuthority()
                                )
                        );

        if (!isAdmin) {
            throw new AccessDeniedException(
                    "Administrator access is required"
            );
        }
    }

    private CardApplicationResponse toResponse(
            CardApplication application,
            Long cardId
    ) {

        return CardApplicationResponse.builder()
                .id(application.getId())
                .applicantEmail(application.getApplicantEmail())
                .accountNumber(application.getAccountNumber())
                .cardType(application.getCardType())
                .holderName(application.getHolderName())
                .applicationStatus(application.getApplicationStatus())
                .rejectionReason(application.getRejectionReason())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .reviewedAt(application.getReviewedAt())
                .cardId(cardId)
                .build();
    }
}
