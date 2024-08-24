package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("select u from User u where u.fullName like %:query% or u.email like %:query%")
    List<User> searchUser(@Param("query") String query);
}
