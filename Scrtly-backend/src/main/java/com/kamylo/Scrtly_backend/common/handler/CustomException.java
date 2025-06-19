package com.kamylo.Scrtly_backend.common.handler;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    BusinessErrorCodes errorCode;

    public CustomException(BusinessErrorCodes businessErrorCodes) {
        super(businessErrorCodes.getDescription());
        this.errorCode = businessErrorCodes;
    }

    public CustomException(BusinessErrorCodes businessErrorCodes, Throwable cause) {
        super(businessErrorCodes.getDescription(), cause);
        this.errorCode = businessErrorCodes;
    }

}
