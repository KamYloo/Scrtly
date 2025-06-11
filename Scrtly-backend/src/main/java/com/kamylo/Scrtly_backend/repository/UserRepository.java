package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"artistEntity", "refreshToken", "roles", "followers", "followings"})
    Optional<UserEntity> findByEmail(String username);
    @EntityGraph(attributePaths = {"artistEntity", "refreshToken", "roles", "followers", "followings"})
    Optional<UserEntity> findByNickName(String nickname);

    @Query("select u from UserEntity u where u.fullName like %:query% or u.nickName like %:query%")
    Set<UserEntity> searchUser(@Param("query") String query);
}
