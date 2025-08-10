package com.kamylo.Scrtly_backend.like.service.impl;

import com.kamylo.Scrtly_backend.like.mapper.SongLikeMapper;
import com.kamylo.Scrtly_backend.like.service.SongLikeService;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.playList.service.PlayListService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SongLikeServiceImpl implements SongLikeService {

    private final UserService userService;
    private final SongLikeRepository songLikeRepository;
    private final PlayListService playListService;
    private final SongRepository songRepository;
    private final SongLikeMapper songLikeMapper;

    @Override
    @Transactional
    public SongLikeDto likeSong(Long songId, String username) {
        UserEntity user = userService.findUserByEmail(username);
        SongEntity song = songRepository.findById(songId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.SONG_NOT_FOUND));
        SongLikeEntity existingLike = songLikeRepository.findByUserIdAndSongId(user.getId(), songId);

        if (existingLike != null) {
            song.setFavorite(false);
            songLikeRepository.delete(existingLike);
            playListService.removeFromFavourites(user, song);
            return songLikeMapper.toDto(existingLike);
        } else {
            song.setFavorite(true);
            playListService.addToFavourites(user, song);
            SongLikeEntity newLike = SongLikeEntity.builder()
                    .song(song)
                    .user(user)
                    .build();
            songLikeRepository.save(newLike);
            return songLikeMapper.toDto(newLike);
        }
    }
}
