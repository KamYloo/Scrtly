package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.chat.service.ChatMessageService;
import com.kamylo.Scrtly_backend.chat.web.controller.ChatMessageController;
import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private ChatMessageController controller;

    @Test
    void getChatMessages_passesPageable_andReturnsOk() {
        Integer chatId = 42;
        ChatMessageDto dto = mock(ChatMessageDto.class);
        when(chatMessageService.getChatMessages(eq(chatId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        Pageable pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "createDate");
        var resp = controller.getChatMessages(chatId, pageable);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(chatMessageService).getChatMessages(eq(chatId), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(1, p.getPageNumber());
        assertEquals(5, p.getPageSize());
    }
}