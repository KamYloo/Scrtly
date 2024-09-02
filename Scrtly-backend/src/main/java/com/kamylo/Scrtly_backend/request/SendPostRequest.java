package com.kamylo.Scrtly_backend.request;


import com.kamylo.Scrtly_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendPostRequest {
    private String description;
    private String image;
    private User user;
}
