package com.kamylo.Scrtly_backend.response;

import lombok.Data;

@Data
public class SortInfo {
    private String property;
    private String direction;

    public SortInfo(String property, String direction) {
        this.property = property;
        this.direction = direction;
    }
}
