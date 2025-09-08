package com.kamylo.Scrtly_backend.album.repository;

import com.kamylo.Scrtly_backend.album.domain.AlbumEntity;
import org.springframework.data.jpa.domain.Specification;

public class AlbumSpecification {
    public static Specification<AlbumEntity> artistContains(String pseudonym) {
        return (root, query, criteriaBuilder) ->
                pseudonym == null || pseudonym.trim().isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("artist").get("pseudonym")), "%" + pseudonym.trim().toLowerCase() + "%");
    }

    public static Specification<AlbumEntity> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                title == null || title.trim().isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
    }
}
