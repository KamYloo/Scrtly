package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.response.PagedResponse;
import com.kamylo.Scrtly_backend.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<PagedResponse<ChatMessageDto>> getChatMessages(@PathVariable Integer chatId,
                                                                         @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
                                                                         Pageable pageable,
                                                                         Principal principal) {
        Page<ChatMessageDto> messages = chatMessageService.getChatMessages(chatId, principal.getName(), pageable);
        return new ResponseEntity<>(PagedResponse.of(messages), HttpStatus.OK);
    }
}
