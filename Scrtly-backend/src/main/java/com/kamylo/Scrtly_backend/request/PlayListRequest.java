package com.kamylo.Scrtly_backend.request;

import com.kamylo.Scrtly_backend.model.User;
import lombok.Data;

@Data
public class PlayListRequest {
    private String title;
    private User user;
}
