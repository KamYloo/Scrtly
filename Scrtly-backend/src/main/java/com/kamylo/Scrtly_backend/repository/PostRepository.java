package com.kamylo.Scrtly_backend.repository;


import com.kamylo.Scrtly_backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.user.id = :userId")
    List<Post> findPostByUserId(@Param("userId") Long userId);
}
