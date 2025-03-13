package com.kamylo.Scrtly_backend.handler;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    BusinessErrorCodes errorCode;

    public CustomException(BusinessErrorCodes businessErrorCodes) {
        super(businessErrorCodes.getDescription());
        this.errorCode = businessErrorCodes;
    }
}
