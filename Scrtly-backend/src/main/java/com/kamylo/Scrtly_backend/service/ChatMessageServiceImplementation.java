package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.exception.ChatException;
import com.kamylo.Scrtly_backend.exception.MessageException;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.model.ChatMessage;
import com.kamylo.Scrtly_backend.model.ChatRoom;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageServiceImplementation implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatService chatService;

    public ChatMessageServiceImplementation(ChatMessageRepository chatMessageRepository, UserService userService, ChatService chatService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Override
    public ChatMessage sendMessage(SendMessageRequest request) throws UserException, ChatException {
        User user = userService.findUserById(request.getUserId());
        ChatRoom chatRoom = chatService.findChatById(request.getChatId());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setChat(chatRoom);
        chatMessage.setMessageText(request.getMessage());
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatsMessages(Integer chatId) throws ChatException {
        ChatRoom chatRoom = chatService.findChatById(chatId);

        return chatMessageRepository.findByChatId(chatRoom.getId());
    }

    @Override
    public ChatMessage findChatMessageById(Integer messageId) throws MessageException {
        Optional<ChatMessage> chatMessage = chatMessageRepository.findById(messageId);
        if (chatMessage.isPresent()) {
            return chatMessage.get();
        }

        throw new MessageException("Message not found with id " + messageId);
    }

    @Override
    public void deleteChatMessageById(Integer messageId, User reqUser) throws MessageException {
        ChatMessage chatMessage = findChatMessageById(messageId);

        if (chatMessage.getUser().getId().equals(reqUser.getId())) {
            chatMessageRepository.deleteById(messageId);
        }

        throw new MessageException("You can't delete another user's message" + reqUser.getFullName());
    }
}
