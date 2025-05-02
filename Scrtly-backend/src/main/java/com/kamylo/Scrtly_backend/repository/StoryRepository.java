package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.StoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<StoryEntity, Long> {
    List<StoryEntity> getStoriesByUserId(Long userId);

    @Query("select s from StoryEntity s where s.user in (select u from UserEntity u join u.followers f where f.id = :userId)")
    List<StoryEntity> getStoriesByFollowedUsers(@Param("userId") Long userId);
}
