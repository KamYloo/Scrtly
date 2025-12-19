package com.kamylo.Scrtly_backend.user.repository;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"artistEntity", "refreshToken", "roles"})
    Optional<UserEntity> findByEmail(String username);

    @EntityGraph(attributePaths = {"artistEntity", "refreshToken", "roles"})
    Optional<UserEntity> findByNickName(String nickname);

    @Query("select u from UserEntity u where u.fullName like %:query% or u.nickName like %:query%")
    Set<UserEntity> searchUser(@Param("query") String query);

    @Query("select f from UserEntity u join u.followers f " +
            "where u.id = :userId " +
            "and ( :query is null or :query = '' or lower(f.fullName) like concat('%', lower(:query), '%') )")
    Page<UserEntity> findFollowersByUserId(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    @Query("SELECT count(f) FROM UserEntity u JOIN u.followers f WHERE u.id = :userId")
    long countFollowers(@Param("userId") Long userId);

    @Query("SELECT count(f) FROM UserEntity u JOIN u.followings f WHERE u.id = :userId")
    long countFollowings(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM UserEntity u JOIN u.followers f " +
            "WHERE u.id = :userId AND f.id = :followerId")
    boolean isFollowedBy(@Param("userId") Long userId, @Param("followerId") Long followerId);
}
