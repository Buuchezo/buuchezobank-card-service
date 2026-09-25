package com.buuchezo.cardservice.service;

import com.buuchezo.cardservice.client.AccountClient;
import com.buuchezo.cardservice.client.dto.AccountDto;
import com.buuchezo.cardservice.client.dto.ApiResponse;
import com.buuchezo.cardservice.client.dto.BusinessMembershipDto;

import feign.FeignException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountOwnershipService {

    private final AccountClient accountClient;

    public void verifyAccountAccess(String accountNumber) {

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

        if (hasAdminAuthority(authentication)) {
            return;
        }

        String authenticatedEmail =
                authentication.getName();

        AccountDto account =
                getAccount(accountNumber);

        if (account == null) {
            throw new AccessDeniedException(
                    "You are not authorized to access this account"
            );
        }

        /*
         * =========================================================
         * PERSONAL ACCOUNT
         * =========================================================
         */

        if ("PERSONAL".equalsIgnoreCase(
                account.getOwnershipType())) {

            if (account.getOwnerEmail() == null ||
                    !authenticatedEmail.equalsIgnoreCase(
                            account.getOwnerEmail())) {

                throw new AccessDeniedException(
                        "You are not authorized to access this account"
                );
            }

            return;
        }

        /*
         * =========================================================
         * BUSINESS ACCOUNT
         * =========================================================
         */

        if ("BUSINESS".equalsIgnoreCase(
                account.getOwnershipType())) {

            if (account.getBusinessId() == null) {

                throw new AccessDeniedException(
                        "Business account is missing business information"
                );
            }

            List<BusinessMembershipDto> memberships;

            try {

                memberships =
                        accountClient.getBusinessMembers(
                                account.getBusinessId()
                        );

            } catch (FeignException e) {

                throw new AccessDeniedException(
                        "Unable to verify business membership"
                );
            }

            boolean authorized =
                    memberships != null &&
                    memberships.stream()
                            .anyMatch(membership ->
                                    membership.isActive()
                                            &&
                                    authenticatedEmail.equalsIgnoreCase(
                                            membership.getUserEmail()
                                    )
                                            &&
                                    isBusinessAccountRoleAllowed(
                                            membership.getRole()
                                    )
                            );

            if (!authorized) {

                throw new AccessDeniedException(
                        "You are not authorized to access this business account"
                );
            }

            return;
        }

        throw new AccessDeniedException(
                "Unknown account ownership type"
        );
    }

    private boolean isBusinessAccountRoleAllowed(
            String role
    ) {

        return "OWNER".equalsIgnoreCase(role)
                ||
                "ADMIN".equalsIgnoreCase(role)
                ||
                "ACCOUNTANT".equalsIgnoreCase(role);
    }

    private AccountDto getAccount(
            String accountNumber
    ) {

        try {

            ApiResponse<AccountDto> response =
                    accountClient.getAccountByAccountNumber(
                            accountNumber
                    );

            if (response == null ||
                    response.getData() == null) {

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

    private boolean hasAdminAuthority(
            Authentication authentication
    ) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ADMIN".equals(
                                authority.getAuthority()
                        )
                                ||
                        "ROLE_ADMIN".equals(
                                authority.getAuthority()
                        )
                );
    }
}
