package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.chat.service.ChatService;
import com.kamylo.Scrtly_backend.chat.web.controller.ChatController;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.chat.web.dto.request.ChatRoomRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController controller;

    @Mock
    private Principal principal;

    private ChatRoomDto sampleRoom(Integer id, String name) {
        return ChatRoomDto.builder()
                .id(id)
                .chatRoomName(name)
                .participants(List.of())
                .build();
    }

    @Test
    void createChatRoom_returnsCreated_andCallsService() {
        when(principal.getName()).thenReturn("alice");
        ChatRoomRequest req = ChatRoomRequest.builder()
                .userIds(List.of(2L, 3L))
                .chatRoomName("Group")
                .build();

        ChatRoomDto dto = sampleRoom(1, "Group");
        when(chatService.createChat("alice", req)).thenReturn(dto);

        var resp = controller.createChatRoom(req, principal);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(chatService).createChat("alice", req);
    }

    @Test
    void getChatById_returnsOk_andCallsService() {
        when(principal.getName()).thenReturn("bob");
        ChatRoomDto dto = sampleRoom(5, "Private");
        when(chatService.getChatById(5, "bob")).thenReturn(dto);

        var resp = controller.getChatById(5, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
        verify(chatService).getChatById(5, "bob");
    }

    @Test
    void getChatsByReqUser_returnsOk_andCallsService() {
        when(principal.getName()).thenReturn("carol");
        ChatRoomDto dto = sampleRoom(2, "x");
        when(chatService.findChatsByUser("carol")).thenReturn(List.of(dto));

        var resp = controller.getChatsByReqUser(principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(List.of(dto), resp.getBody());
        verify(chatService).findChatsByUser("carol");
    }

    @Test
    void deleteChatHandler_callsService_andReturnsOkWithId() {
        when(principal.getName()).thenReturn("owner");
        Integer chatId = 7;

        var resp = controller.deleteChatHandler(chatId, principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(chatId, resp.getBody());
        verify(chatService).deleteChat(chatId, "owner");
    }
}