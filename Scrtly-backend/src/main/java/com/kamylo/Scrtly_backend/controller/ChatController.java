package com.kamylo.Scrtly_backend.controller;


import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.request.ChatRoomRequest;
import com.kamylo.Scrtly_backend.response.ApiResponse;
import com.kamylo.Scrtly_backend.service.ChatService;
import com.kamylo.Scrtly_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/chatRoom")
    public ResponseEntity<ChatRoom> createChatRoomHandler(@RequestBody ChatRoomRequest chatRoomRequest, @RequestHeader("Authorization")String token) throws UserException {
        User reqUser = userService.findUserProfileByJwt(token);

        ChatRoom chatRoom = chatService.createChat(reqUser, chatRoomRequest.getUserId());

        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatRoom> findChatByIdHandler(@PathVariable Integer chatId, @RequestHeader("Authorization")String token) throws ChatException {
        ChatRoom chatRoom = chatService.findChatById(chatId);

        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ChatRoom>> findAllChatByUserIdHandler(@RequestHeader("Authorization")String token) throws UserException {
        User reqUser = userService.findUserProfileByJwt(token);

        List<ChatRoom> chatRoomList = chatService.findAllChatByUserId(reqUser.getId());

        return new ResponseEntity<>(chatRoomList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<ApiResponse> deleteChatHandler(@PathVariable Integer chatId, @RequestHeader("Authorization")String token) throws UserException, ChatException {
        User reqUser = userService.findUserProfileByJwt(token);

        chatService.deleteChat(chatId, reqUser.getId());
        ApiResponse response = new ApiResponse("chat is deleted successfully", true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
