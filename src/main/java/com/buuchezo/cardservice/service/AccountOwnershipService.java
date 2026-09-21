package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.client.AccountClient;
import com.buuchezo.cardservice.client.dto.AccountDto;
import com.buuchezo.cardservice.client.dto.ApiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountOwnershipService {

    private final AccountClient accountClient;

    public void verifyAccountAccess(String accountNumber) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        if (hasAdminAuthority(authentication)) {
            return;
        }

        String authenticatedEmail = authentication.getName();

        AccountDto account = getAccount(accountNumber);

        if (account == null || account.getOwnerEmail() == null) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account"
            );
        }

        if (!authenticatedEmail.equalsIgnoreCase(account.getOwnerEmail())) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account"
            );
        }
    }

    private AccountDto getAccount(String accountNumber) {

        try {
            ApiResponse<AccountDto> response =
                    accountClient.getAccountByAccountNumber(accountNumber);

            if (response == null || response.getData() == null) {
                throw new AccessDeniedException(
                        "Unable to verify account ownership"
                );
            }

            return response.getData();

        } catch (FeignException.NotFound e) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account"
            );
        } catch (FeignException e) {
            throw new AccessDeniedException(
                    "Unable to verify account ownership"
            );
        }
    }

    private boolean hasAdminAuthority(Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ADMIN".equals(authority.getAuthority())
                );
    }
}
