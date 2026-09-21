package com.buuchezo.cardservice.repository;

import com.buuchezo.cardservice.entity.CardApplication;
import com.buuchezo.cardservice.enums.CardApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {

    List<CardApplication> findByApplicantEmailOrderByCreatedAtDesc(String applicantEmail);

    List<CardApplication> findByApplicationStatusOrderByCreatedAtAsc(
            CardApplicationStatus applicationStatus
    );

    Optional<CardApplication> findByIdAndApplicantEmail(
            Long id,
            String applicantEmail
    );

    boolean existsByAccountNumberAndApplicantEmailAndApplicationStatus(
            String accountNumber,
            String applicantEmail,
            CardApplicationStatus applicationStatus
    );
}
