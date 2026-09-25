package com.buuchezo.cardservice.client;

import com.buuchezo.cardservice.client.dto.AccountDto;
import com.buuchezo.cardservice.client.dto.ApiResponse;
import com.buuchezo.cardservice.client.dto.BusinessMembershipDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "USER-ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/api/accounts/{accountNumber}")
    ApiResponse<AccountDto> getAccountByAccountNumber(
            @PathVariable("accountNumber") String accountNumber
    );

    @GetMapping("/api/businesses/{businessId}/members")
    List<BusinessMembershipDto> getBusinessMembers(
            @PathVariable("businessId") Long businessId
    );
}
