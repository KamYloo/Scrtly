package com.kamylo.Scrtly_backend.repository;

import com.kamylo.Scrtly_backend.model.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, Integer> {
    List<PlayList> findAllByOrderByIdDesc();
}
