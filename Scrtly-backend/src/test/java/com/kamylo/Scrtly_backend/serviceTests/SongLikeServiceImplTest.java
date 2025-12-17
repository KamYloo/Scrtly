package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.like.domain.SongLikeEntity;
import com.kamylo.Scrtly_backend.like.mapper.SongLikeMapper;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.like.service.impl.SongLikeServiceImpl;
import com.kamylo.Scrtly_backend.like.web.dto.SongLikeDto;
import com.kamylo.Scrtly_backend.playList.service.PlayListService;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongLikeServiceImplTest {

    @Mock private UserService userService;
    @Mock private SongLikeRepository songLikeRepository;
    @Mock private PlayListService playListService;
    @Mock private SongRepository songRepository;
    @Mock private SongLikeMapper songLikeMapper;

    @InjectMocks private SongLikeServiceImpl songLikeService;

    private UserEntity user(long id) {
        return UserEntity.builder()
                .id(id)
                .email("user" + id + "@example.com")
                .build();
    }

    private SongEntity song(long id) {
        return SongEntity.builder()
                .id(id)
                .build();
    }

    private SongLikeEntity likeEntity(long id, UserEntity u, SongEntity s) {
        return SongLikeEntity.builder()
                .id(id)
                .user(u)
                .song(s)
                .build();
    }

    private SongLikeDto dto(long id) {
        return SongLikeDto.builder()
                .id(id)
                .build();
    }

    @Test
    void likeSong_shouldUnlike_whenAlreadyLiked() {
        Long songId = 10L;
        String username = "u@example.com";
        UserEntity u = user(3L);
        SongEntity s = song(songId);
        SongLikeEntity existingLike = likeEntity(100L, u, s);
        SongLikeDto expectedDto = dto(100L);

        when(userService.findUserByEmail(username)).thenReturn(u);
        when(songRepository.findById(songId)).thenReturn(Optional.of(s));
        when(songLikeRepository.findByUserIdAndSongId(u.getId(), songId)).thenReturn(existingLike);
        when(songLikeMapper.toDto(existingLike)).thenReturn(expectedDto);

        SongLikeDto result = songLikeService.likeSong(songId, username);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());

        verify(songLikeRepository).delete(existingLike);
        verify(playListService).removeFromFavourites(u, s);
        verify(songLikeRepository, never()).save(any(SongLikeEntity.class));
        verify(playListService, never()).addToFavourites(any(), any());
    }

    @Test
    void likeSong_shouldLike_whenNotPreviouslyLiked() {
        Long songId = 20L;
        String username = "artist@example.com";
        UserEntity u = user(5L);
        SongEntity s = song(songId);
        SongLikeDto returnedDto = dto(777L);

        when(userService.findUserByEmail(username)).thenReturn(u);
        when(songRepository.findById(songId)).thenReturn(Optional.of(s));
        when(songLikeRepository.findByUserIdAndSongId(u.getId(), songId)).thenReturn(null);
        when(songLikeRepository.save(any(SongLikeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(songLikeMapper.toDto(any(SongLikeEntity.class))).thenReturn(returnedDto);

        SongLikeDto result = songLikeService.likeSong(songId, username);

        assertNotNull(result);
        assertEquals(returnedDto.getId(), result.getId());

        ArgumentCaptor<SongLikeEntity> captor = ArgumentCaptor.forClass(SongLikeEntity.class);
        verify(songLikeRepository).save(captor.capture());
        SongLikeEntity capturedEntity = captor.getValue();

        assertEquals(u, capturedEntity.getUser());
        assertEquals(s, capturedEntity.getSong());

        verify(playListService).addToFavourites(u, s);
        verify(songLikeRepository, never()).delete(any());
        verify(playListService, never()).removeFromFavourites(any(), any());
    }

    @Test
    void likeSong_shouldThrow_whenSongNotFound() {
        Long songId = 99L;
        String username = "noone@example.com";
        UserEntity u = user(7L);

        when(userService.findUserByEmail(username)).thenReturn(u);
        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> songLikeService.likeSong(songId, username));
        assertEquals(BusinessErrorCodes.SONG_NOT_FOUND, ex.getErrorCode());

        verify(songLikeRepository, never()).findByUserIdAndSongId(anyLong(), anyLong());
        verify(songLikeRepository, never()).save(any());
        verify(songLikeRepository, never()).delete(any());
        verifyNoInteractions(playListService);
    }
}