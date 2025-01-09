package com.kamylo.Scrtly_backend.specification;

import com.kamylo.Scrtly_backend.entity.CommentEntity;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CommentSpecification {
    public static Specification<CommentEntity> byPostId(Long postId) {
        return (root, query, criteriaBuilder) ->
                postId == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("post").get("id"), postId);
    }

    public static Specification<CommentEntity> orderByLatestActivity() {
        return (root, query, criteriaBuilder) -> {
            Expression<LocalDateTime> createDate = root.get("createDate").as(LocalDateTime.class);
            Expression<LocalDateTime> lastModifiedDate = root.get("lastModifiedDate").as(LocalDateTime.class);

            Expression<LocalDateTime> maxDate = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.greaterThan(createDate, lastModifiedDate), createDate)
                    .otherwise(lastModifiedDate).as(LocalDateTime.class);

            query.orderBy(criteriaBuilder.desc(maxDate));
            return criteriaBuilder.conjunction();
        };
    }


    public static Specification<CommentEntity> orderByLikes() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(root.get("likes"))));
            return criteriaBuilder.conjunction();
        };
    }
}
