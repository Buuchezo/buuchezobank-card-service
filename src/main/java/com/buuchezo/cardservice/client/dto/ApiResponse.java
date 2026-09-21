package com.buuchezo.cardservice.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
}
