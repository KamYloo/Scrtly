package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, Integer> {
    List<PlayList> findAllByOrderByIdDesc();
    Optional<PlayList> findByUserAndFavourite(User user, boolean isFavourite);
}
