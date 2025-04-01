package com.kamylo.Scrtly_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayListRequest {
    private Integer playListId;
    private String title;
}
