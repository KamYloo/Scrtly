package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Integer chatId, Principal principal) {
        List<ChatMessageDto> chatMessages = chatMessageService.getChatsMessages(chatId, principal.getName());
        return new  ResponseEntity<>(chatMessages, HttpStatus.OK);
    }
}
