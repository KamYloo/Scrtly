package com.kamylo.Scrtly_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequest {
    private String fullName;
    private String profilePicture;

    public UpdateUserRequest(String fullName, String profilePicture) {
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

}
