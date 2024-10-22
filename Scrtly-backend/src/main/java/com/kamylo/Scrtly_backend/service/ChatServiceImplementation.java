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
        ChatRoom isChatExist = chatRepository.findSingleChatRoomById(reqUser, user);

        System.out.println(isChatExist);
        if (isChatExist != null) {
            return isChatExist;
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setFirstPerson(reqUser);
        chatRoom.setSecondPerson(user);

        return chatRepository.save(chatRoom);
    }

    @Override
    public ChatRoom findChatById(Integer chatId) throws ChatException {
        return chatRepository.findById(chatId).orElseThrow(() -> new ChatException("Chat not found with id " + chatId));
    }

    @Override
    public List<ChatRoom> findAllChatByUserId(Long userId) throws UserException {

        return chatRepository.findChatRoomById(userId);
    }

    @Override
    public void deleteChat(Integer chatId, Long userId) throws ChatException, UserException {
        ChatRoom chatRoom = findChatById(chatId);

        if (chatRoom == null) {
            throw new ChatException("Chat not found with id " + chatId);
        }
        if (!userId.equals(chatRoom.getFirstPerson().getId()) && !userId.equals(chatRoom.getSecondPerson().getId())) {
            throw new UserException("You do not have permission to delete this chat");
        }

        chatRepository.deleteById(chatId);
    }
}
