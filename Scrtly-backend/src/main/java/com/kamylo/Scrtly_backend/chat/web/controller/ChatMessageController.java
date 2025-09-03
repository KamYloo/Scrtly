package com.kamylo.Scrtly_backend.chat.web.controller;

import com.kamylo.Scrtly_backend.chat.web.dto.ChatMessageDto;
import com.kamylo.Scrtly_backend.common.response.PagedResponse;
import com.kamylo.Scrtly_backend.chat.service.ChatMessageService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<PagedResponse<ChatMessageDto>> getChatMessages( @PathVariable @Positive(message = "chatId must be positive") Integer chatId,
                                                                          @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
                                                                          Pageable pageable)  {
        Page<ChatMessageDto> messages = chatMessageService.getChatMessages(chatId, pageable);
        return new ResponseEntity<>(PagedResponse.of(messages), HttpStatus.OK);
    }
}
