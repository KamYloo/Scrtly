package com.kamylo.Scrtly_backend.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"),
    ARTIST_VERIFICATION("artist_verification"),
    ;

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
