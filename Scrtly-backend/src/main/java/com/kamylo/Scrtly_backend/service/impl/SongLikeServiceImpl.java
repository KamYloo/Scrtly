package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.entity.SongEntity;
import com.kamylo.Scrtly_backend.entity.SongLikeEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.repository.SongRepository;
import com.kamylo.Scrtly_backend.service.PlayListService;
import com.kamylo.Scrtly_backend.service.SongLikeService;
import com.kamylo.Scrtly_backend.service.UserService;
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
    private final Mapper<SongLikeEntity, SongLikeDto> songLikeMapper;

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
            return songLikeMapper.mapTo(existingLike);
        } else {
            song.setFavorite(true);
            playListService.addToFavourites(user, song);
            SongLikeEntity newLike = SongLikeEntity.builder()
                    .song(song)
                    .user(user)
                    .build();
            songLikeRepository.save(newLike);
            return songLikeMapper.mapTo(newLike);
        }
    }
}
