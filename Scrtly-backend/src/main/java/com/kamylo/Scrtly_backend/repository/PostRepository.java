package com.kamylo.Scrtly_backend.repository;


import com.kamylo.Scrtly_backend.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    @EntityGraph(attributePaths = {"user"})
    Page<PostEntity> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable);
}
