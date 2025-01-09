package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
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

    @PostMapping("/create")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody SendMessageRequest request, Principal principal) {
        ChatMessageDto chatMessage = chatMessageService.sendMessage(request,principal.getName());
        return new  ResponseEntity<>(chatMessage, HttpStatus.CREATED);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Integer chatId, Principal principal) {
        List<ChatMessageDto> chatMessages = chatMessageService.getChatsMessages(chatId, principal.getName());
        return new  ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId, Principal principal) {
        chatMessageService.deleteChatMessage(messageId, principal.getName());
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
