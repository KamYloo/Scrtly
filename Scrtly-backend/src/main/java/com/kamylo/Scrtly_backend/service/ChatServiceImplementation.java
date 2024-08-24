package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImplementation implements ChatService {


    private final ChatRepository chatRepository;

    private final UserService userService;

    public ChatServiceImplementation(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    @Override
    public ChatRoom createChat(User reqUser, Long userId2) throws UserException {
        User user = userService.findUserById(userId2);
        ChatRoom isChatExist = chatRepository.findSingleChatRoomById(user, reqUser);

        if (isChatExist != null) {
            return isChatExist;
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setFirstPerson(reqUser);
        chatRoom.setSecondPerson(user);

        return chatRoom;
    }

    @Override
    public ChatRoom findChatById(Integer chatId) throws ChatException {
        Optional<ChatRoom> chatRoom = chatRepository.findById(chatId);

        if (chatRoom.isPresent()) {
            return chatRoom.get();
        }

        throw new ChatException("Chat not found with id " + chatId);
    }

    @Override
    public List<ChatRoom> findAllChatByUserId(Long userId) throws UserException {
        User user = userService.findUserById(userId);
        return chatRepository.findChatRoomById(user.getId());
    }

    @Override
    public void deleteChat(Integer chatId, Long userId) {
        Optional<ChatRoom> chatRoom = chatRepository.findById(chatId);
        if (chatRoom.isPresent()) {
            ChatRoom chatRoomToDelete = chatRoom.get();
            chatRepository.deleteById(chatRoomToDelete.getId());
        }
    }
}
