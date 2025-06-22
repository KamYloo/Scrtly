package com.kamylo.Scrtly_backend.playList.service;

import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import com.kamylo.Scrtly_backend.playList.domain.PlayListEntity;
import com.kamylo.Scrtly_backend.common.service.impl.FileServiceImpl;
import com.kamylo.Scrtly_backend.song.domain.SongEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.playList.repository.PlayListRepository;
import com.kamylo.Scrtly_backend.like.repository.SongLikeRepository;
import com.kamylo.Scrtly_backend.song.repository.SongRepository;
import com.kamylo.Scrtly_backend.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlayListService {

    private final UserService userService;
    private final FileServiceImpl fileService;
    private final PlayListRepository playListRepository;
    private final SongRepository songRepository;
    private final SongLikeRepository songLikeRepository;
    private final Mapper<PlayListEntity, PlayListDto> playListMapper;
    private final Mapper<SongEntity, SongDto> songMapper;

    @Override
    @Transactional
    public PlayListDto createPlayList(String title, String username, MultipartFile playListImage) {
        UserEntity user = userService.findUserByEmail(username);
        String imagePath = null;
        if (!playListImage.isEmpty()) {
            imagePath = fileService.saveFile(playListImage, "playListImages/");
        }

        PlayListEntity playListEntity = PlayListEntity.builder()
                .title(title)
                .user(user)
                .coverImage(imagePath)
                .build();

        PlayListEntity savedPlayListEntity = playListRepository.save(playListEntity);
        return playListMapper.mapTo(savedPlayListEntity);
    }

    @Override
    public PlayListDto getPlayList(Integer playListId) {
        PlayListEntity playList = playListRepository.findById(playListId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.PLAYLIST_NOT_FOUND));
        return playListMapper.mapTo(playList);
    }

    @Override
    public Page<PlayListDto> getPlayLists(Pageable pageable) {
        return playListRepository.findAll(pageable).map(playListMapper::mapTo);
    }

    @Override
    public Page<PlayListDto> getPlayListsByUser(String username, Pageable pageable) {
        UserEntity user = userService.findUserByEmail(username);
        return playListRepository.getPlayListsByUserId(user.getId(), pageable).map(playListMapper::mapTo);
    }

    @Override
    public PlayListDto addSongToPlayList(Long songId, Integer playListId, String username) {
        SongEntity songEntity = songRepository.findById(songId).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.SONG_NOT_FOUND));
        PlayListEntity playList = playListRepository.findById(playListId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.PLAYLIST_NOT_FOUND));
        if (validatePlayListOwnership(username, playList)) {
            if (playList.getSongs().contains(songEntity)) {
                throw new CustomException(BusinessErrorCodes.SONG_EXISTS);
            } else {
                playList.getSongs().add(songEntity);
                return playListMapper.mapTo(playListRepository.save(playList));
            }
        } else {
            throw new CustomException(BusinessErrorCodes.PLAYLIST_MISMATCH);
        }
    }

    @Override
    public void addToFavourites(UserEntity userEntity, SongEntity songEntity) {
        PlayListEntity favourites = findOrCreateFavouritePlayList(userEntity);
        if (!favourites.getSongs().contains(songEntity)) {
            favourites.getSongs().add(songEntity);
            playListRepository.save(favourites);
        }
    }

    @Override
    public void removeFromFavourites(UserEntity userEntity, SongEntity songEntity) {
        PlayListEntity favourites = findOrCreateFavouritePlayList(userEntity);
        favourites.getSongs().remove(songEntity);
        playListRepository.save(favourites);
    }

    @Override
    public void removeSongFromPlayList(Long songId, Integer playListId, String username) {
        SongEntity songEntity = songRepository.findById(songId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.SONG_NOT_FOUND));

        PlayListEntity playList = playListRepository.findById(playListId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.PLAYLIST_NOT_FOUND));
        if (validatePlayListOwnership(username, playList)) {
            if(playList.getSongs().contains(songEntity)) {
                playList.getSongs().remove(songEntity);
                playListRepository.save(playList);
            } else {
                throw new CustomException(BusinessErrorCodes.SONG_NOT_EXISTS);
            }
        } else {
            throw new CustomException(BusinessErrorCodes.PLAYLIST_MISMATCH);
        }
    }

    @Override
    public Page<SongDto> getPlayListTracks(Integer playListId, Pageable pageable) {
        return songRepository.findByPlaylistId(playListId, pageable).map(songMapper::mapTo);
    }

    @Override
    @Transactional
    public PlayListDto updatePlayList(Integer playListId, String title, String username, MultipartFile playListImage) {
        PlayListEntity playListEntity = playListRepository.findById(playListId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.PLAYLIST_NOT_FOUND));
        if (validatePlayListOwnership(username, playListEntity)) {
            if (title != null && !title.isEmpty()) {
                playListEntity.setTitle(title);
            }
            if (playListImage != null && !playListImage.isEmpty()) {
                String imagePath = fileService.updateFile(playListImage, playListEntity.getCoverImage(), "playListImages/");
                playListEntity.setCoverImage(imagePath);
            }
            return playListMapper.mapTo(playListRepository.save(playListEntity));
        } else {
            throw new CustomException(BusinessErrorCodes.PLAYLIST_MISMATCH);
        }
    }

    @Override
    @Transactional
    public void deletePlayList(Integer playListId, String username) {
        PlayListEntity playListEntity = playListRepository.findById(playListId)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.PLAYLIST_NOT_FOUND));
        if (validatePlayListOwnership(username, playListEntity)) {
            if (playListEntity.isFavourite()) {
                for (SongEntity songEntity : playListEntity.getSongs()) {
                    songEntity.setFavorite(false);
                    songLikeRepository.deleteBySong(songEntity);
                }
            }
            fileService.deleteFile(playListEntity.getCoverImage());
            playListRepository.delete(playListEntity);
        } else {
            throw new CustomException(BusinessErrorCodes.PLAYLIST_MISMATCH);
        }
    }

    public PlayListEntity findOrCreateFavouritePlayList(UserEntity user) {
        return playListRepository.findByUserIdAndFavourite(user.getId(), true)
                .orElseGet(() -> {
                    PlayListEntity favouritePlayList = PlayListEntity.builder()
                            .user(user)
                            .favourite(true)
                            .title("Favourite Songs")
                            .songs(new HashSet<>())
                            .build();
                    return playListRepository.save(favouritePlayList);
                });
    }

    private boolean validatePlayListOwnership(String username, PlayListEntity playList) {
        UserEntity user = userService.findUserByEmail(username);
        return user.getId().equals(playList.getUser().getId());
    }
}
