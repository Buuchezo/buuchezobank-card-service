package com.buuchezo.cardservice.entity;

import com.buuchezo.cardservice.enums.CardApplicationStatus;
import com.buuchezo.cardservice.enums.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "card_applications",
        indexes = {
                @Index(name = "idx_card_application_email", columnList = "applicant_email"),
                @Index(name = "idx_card_application_account", columnList = "account_number"),
                @Index(name = "idx_card_application_status", columnList = "application_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "applicant_email", nullable = false, length = 255)
    private String applicantEmail;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType cardType;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 20)
    private CardApplicationStatus applicationStatus;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
