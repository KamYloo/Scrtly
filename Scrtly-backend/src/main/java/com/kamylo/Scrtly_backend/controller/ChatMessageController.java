package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.MessageException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatMessage;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    public ChatMessageController(ChatMessageService chatMessageService, UserService userService) {
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<ChatMessage> sendMessageHandler(@RequestBody SendMessageRequest request, @RequestHeader("Authorization") String token) throws UserException, ChatException {
        User user = userService.findUserProfileByJwt(token);

        request.setUserId(user.getId());
        ChatMessage chatMessage = chatMessageService.sendMessage(request);

        return new  ResponseEntity<>(chatMessage, HttpStatus.OK);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<ChatMessage>> getChatsMessagesHandler(@PathVariable Integer chatId, @RequestHeader("Authorization") String token) throws ChatException {

        List<ChatMessage> chatMessage = chatMessageService.getChatsMessages(chatId);

        return new  ResponseEntity<>(chatMessage, HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable Integer messageId, @RequestHeader("Authorization") String token) throws UserException, MessageException {
        User user = userService.findUserProfileByJwt(token);

        chatMessageService.deleteChatMessageById(messageId, user);
        ApiResponse apiResponse = new ApiResponse("message deleted successfully", false);
        return new  ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
