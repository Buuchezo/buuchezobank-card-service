package com.buuchezo.cardservice.dto;

import com.buuchezo.cardservice.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCardApplicationRequest {

    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must not exceed 20 characters")
    private String accountNumber;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @NotBlank(message = "Holder name is required")
    @Size(max = 100, message = "Holder name must not exceed 100 characters")
    private String holderName;
}
