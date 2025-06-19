package com.kamylo.Scrtly_backend.post.repository;

import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {
    public static Specification<PostEntity> hasMinLikes(Integer minLikes) {
        return (root, query, cb) -> minLikes == null
                ? null
                : cb.greaterThanOrEqualTo(cb.size(root.get("likes")), minLikes);
    }

    public static Specification<PostEntity> hasMaxLikes(Integer maxLikes) {
        return (root, query, cb) -> maxLikes == null
                ? null
                : cb.lessThanOrEqualTo(cb.size(root.get("likes")), maxLikes);
    }
}
