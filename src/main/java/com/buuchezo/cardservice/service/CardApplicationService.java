package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.dto.CardApplicationResponse;
import com.buuchezo.cardservice.dto.CreateCardApplicationRequest;

import java.util.List;

public interface CardApplicationService {

    // Customer
    CardApplicationResponse createApplication(
            CreateCardApplicationRequest request
    );

    List<CardApplicationResponse> getMyApplications();

    CardApplicationResponse getMyApplication(Long id);

    // Admin
    List<CardApplicationResponse> getPendingApplications();

    List<CardApplicationResponse> getAllApplications();

    CardApplicationResponse approveApplication(Long id);

    CardApplicationResponse rejectApplication(
            Long id,
            String rejectionReason
    );
}
