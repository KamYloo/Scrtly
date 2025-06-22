package com.kamylo.Scrtly_backend.like.service;

import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import org.springframework.stereotype.Service;

@Service
public interface SongLikeService {
    SongLikeDto likeSong(Long songId, String username);
}
