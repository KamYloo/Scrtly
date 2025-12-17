package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.playList.service.PlayListService;
import com.kamylo.Scrtly_backend.playList.web.controller.PlayListController;
import com.kamylo.Scrtly_backend.playList.web.dto.PlayListDto;
import com.kamylo.Scrtly_backend.playList.web.dto.request.PlayListCreateRequest;
import com.kamylo.Scrtly_backend.playList.web.dto.request.PlayListUpdateRequest;
import com.kamylo.Scrtly_backend.song.web.dto.SongDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayListControllerTest {

    @Mock
    private PlayListService playListService;

    @InjectMocks
    private PlayListController controller;

    @Mock
    private Principal principal;

    private PlayListDto sampleDto(Integer id, String title) {
        return PlayListDto.builder()
                .id(id)
                .title(title)
                .creationDate(LocalDate.now())
                .coverImage("cover.png")
                .favourite(false)
                .tracksCount(0)
                .totalDuration(0)
                .build();
    }

    @Test
    void createPlayList_returnsCreated_andCallsService() {
        var file = mock(MultipartFile.class);
        when(principal.getName()).thenReturn("alice");
        PlayListCreateRequest req = PlayListCreateRequest.builder()
                .title("My List")
                .file(file)
                .build();

        PlayListDto dto = sampleDto(1, "My List");
        when(playListService.createPlayList(eq("My List"), eq("alice"), eq(file))).thenReturn(dto);

        var resp = controller.createPlayList(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(playListService).createPlayList("My List", "alice", file);
    }

    @Test
    void getPlayLists_returnsPagedAndPassesPageable() {
        PlayListDto dto = sampleDto(2, "P");
        Pageable pageable = PageRequest.of(0, 10);
        when(playListService.getPlayLists(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        var resp = controller.getPlayLists(pageable);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(playListService).getPlayLists(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
    }

    @Test
    void getPlayListsByReqUser_usesPrincipalName_andReturnsPaged() {
        when(principal.getName()).thenReturn("bob");
        PlayListDto dto = sampleDto(3, "UserList");
        Pageable pageable = PageRequest.of(0, 10);
        when(playListService.getPlayListsByUser(eq("bob"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        var resp = controller.getPlayListsByReqUser(pageable, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(playListService).getPlayListsByUser(eq("bob"), any(Pageable.class));
    }

    @Test
    void getPlayList_returnsDto() {
        Integer id = 5;
        PlayListDto dto = sampleDto(id, "Single");
        when(playListService.getPlayList(id)).thenReturn(dto);

        var resp = controller.getPlayList(id);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(playListService).getPlayList(id);
    }

    @Test
    void getPlayListTracks_passesPageableAndUsername_andReturnsPaged() {
        Integer id = 7;
        SongDto s = new SongDto();
        Pageable pageable = PageRequest.of(1, 5);
        String username = "userTrackTest";

        when(principal.getName()).thenReturn(username);
        when(playListService.getPlayListTracks(eq(id), any(Pageable.class), eq(username)))
                .thenReturn(new PageImpl<>(List.of(s)));

        var resp = controller.getPlayListTracks(id, pageable, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        ArgumentCaptor<Pageable> pageCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(playListService).getPlayListTracks(eq(id), pageCaptor.capture(), eq(username));
        assertEquals(1, pageCaptor.getValue().getPageNumber());
        assertEquals(5, pageCaptor.getValue().getPageSize());
    }

    @Test
    void getPlayListTracks_whenPrincipalIsNull_passesNullUsername() {
        Integer id = 77;
        SongDto s = new SongDto();
        Pageable pageable = PageRequest.of(0, 10);

        when(playListService.getPlayListTracks(eq(id), any(Pageable.class), isNull()))
                .thenReturn(new PageImpl<>(List.of(s)));

        var resp = controller.getPlayListTracks(id, pageable, null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(playListService).getPlayListTracks(eq(id), any(Pageable.class), isNull());
    }

    @Test
    void addSongToPlayList_callsService_andReturnsDto() {
        Integer playListId = 8;
        Long songId = 100L;
        when(principal.getName()).thenReturn("carol");
        PlayListDto dto = sampleDto(playListId, "WithSong");
        when(playListService.addSongToPlayList(eq(songId), eq(playListId), eq("carol"))).thenReturn(dto);

        var resp = controller.addSongToPlayList(playListId, songId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(playListService).addSongToPlayList(songId, playListId, "carol");
    }

    @Test
    void deleteSongFromPlayList_callsService_andReturnsOkWithSongId() {
        Integer playListId = 9;
        Long songId = 200L;
        when(principal.getName()).thenReturn("dave");

        var resp = controller.deleteSongFromPlayList(playListId, songId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(songId, resp.getBody());
        verify(playListService).removeSongFromPlayList(songId, playListId, "dave");
    }

    @Test
    void updatePlayList_callsService_andReturnsDto() {
        var file = mock(MultipartFile.class);
        when(principal.getName()).thenReturn("erin");
        PlayListUpdateRequest req = PlayListUpdateRequest.builder()
                .playListId(11)
                .title("UpdatedTitle")
                .file(file)
                .build();

        PlayListDto dto = sampleDto(11, "UpdatedTitle");
        when(playListService.updatePlayList(eq(11), eq("UpdatedTitle"), eq("erin"), eq(file))).thenReturn(dto);

        var resp = controller.updatePlayList(req, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(playListService).updatePlayList(11, "UpdatedTitle", "erin", file);
    }

    @Test
    void deletePlayList_callsService_andReturnsOkWithId() {
        Integer id = 12;
        when(principal.getName()).thenReturn("frank");

        var resp = controller.deletePlayList(id, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(id, resp.getBody());
        verify(playListService).deletePlayList(id, "frank");
    }
}