package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.dto.SongLikeDto;
import org.springframework.stereotype.Service;

@Service
public interface SongLikeService {
    SongLikeDto likeSong(Long songId, String username);
}
