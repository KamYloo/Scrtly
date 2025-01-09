package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatRoomDto;
import com.kamylo.Scrtly_backend.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<ChatRoomDto> createChatRoom(@PathVariable Long userId, Principal principal) {
        ChatRoomDto chatRoom = chatService.createChat(principal.getName(), userId);
        return new ResponseEntity<>(chatRoom, HttpStatus.CREATED);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatRoomDto> getChatById(@PathVariable Integer chatId, Principal principal) {
        ChatRoomDto chatRoom = chatService.getChatById(chatId, principal.getName());
        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ChatRoomDto>> getChatsByReqUser(Principal principal) {
        List<ChatRoomDto> chatRooms = chatService.findChatsByUser(principal.getName());
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<?> deleteChatHandler(@PathVariable Integer chatId, Principal principal) {
        chatService.deleteChat(chatId, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
