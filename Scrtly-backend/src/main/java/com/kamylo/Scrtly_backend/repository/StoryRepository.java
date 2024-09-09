package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    @Query("select s from Story s where s.user.id = :userId")
    List<Story> getAllStoriesByUserId(@Param("userId") Long userId);
}
