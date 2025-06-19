package com.kamylo.Scrtly_backend.story.repository;

import com.kamylo.Scrtly_backend.story.domain.StoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<StoryEntity, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<StoryEntity> getStoriesByUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("select s from StoryEntity s where s.user in (select u from UserEntity u join u.followers f where f.id = :userId)")
    List<StoryEntity> getStoriesByFollowedUsers(@Param("userId") Long userId);
}
