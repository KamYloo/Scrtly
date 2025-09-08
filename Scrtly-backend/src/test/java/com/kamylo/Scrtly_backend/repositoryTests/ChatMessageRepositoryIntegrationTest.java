package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.chat.domain.ChatMessageEntity;
import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.chat.repository.ChatMessageRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChatMessageRepositoryIntegrationTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String nick = (base + "_" + unique).toLowerCase();
        UserEntity user = UserEntity.builder()
                .fullName("Full " + nick)
                .nickName(nick)
                .email(nick + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(user);
        em.flush();
        return user;
    }

    private ChatRoomEntity persistChatRoom(String baseName) {
        String roomName = baseName + "-" + UUID.randomUUID().toString().substring(0, 6);
        ChatRoomEntity room = ChatRoomEntity.builder()
                .chatRoomName(roomName)
                .build();
        em.persist(room);
        em.flush();
        return room;
    }

    private void persistChatMessage(String text, UserEntity user, ChatRoomEntity room, LocalDateTime createDate) {
        ChatMessageEntity msg = ChatMessageEntity.builder()
                .messageText(text)
                .createDate(createDate)
                .lastModifiedDate(createDate)
                .user(user)
                .chatRoom(room)
                .build();
        em.persist(msg);
        em.flush();
    }

    @Test
    void findByChatRoomIdOrderByCreateDateDesc_returnsPagedMessagesOrderedDesc() {
        UserEntity u1 = persistUser("alice");
        UserEntity u2 = persistUser("bob");

        ChatRoomEntity room1 = persistChatRoom("room1");
        ChatRoomEntity room2 = persistChatRoom("room2");

        persistChatMessage("msg-old", u1, room1, LocalDateTime.of(2020, 1, 1, 0, 0));
        persistChatMessage("msg-mid", u2, room1, LocalDateTime.of(2021, 1, 1, 0, 0));
        persistChatMessage("msg-new", u1, room1, LocalDateTime.of(2022, 1, 1, 0, 0));
        persistChatMessage("other-room-msg", u2, room2, LocalDateTime.of(2023, 1, 1, 0, 0));

        Integer room1Id = room1.getId();

        Page<ChatMessageEntity> page = chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(
                room1Id,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createDate"))
        );

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getMessageText()).isEqualTo("msg-new");
        assertThat(page.getContent().get(1).getMessageText()).isEqualTo("msg-mid");

        page.getContent().forEach(m -> {
            assertThat(m.getUser()).isNotNull();
            assertThat(m.getChatRoom()).isNotNull();
        });
    }

    @Test
    void pagination_secondPage_returnsRemaining() {
        UserEntity u = persistUser("u_p");
        ChatRoomEntity room = persistChatRoom("rp");

        persistChatMessage("a", u, room, LocalDateTime.of(2020, 1, 1, 0, 0));
        persistChatMessage("b", u, room, LocalDateTime.of(2021, 1, 1, 0, 0));
        persistChatMessage("c", u, room, LocalDateTime.of(2022, 1, 1, 0, 0));

        Integer roomId = room.getId();

        PageRequest pr0 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createDate"));
        PageRequest pr1 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "createDate"));

        Page<ChatMessageEntity> p0 = chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(roomId, pr0);
        Page<ChatMessageEntity> p1 = chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(roomId, pr1);

        assertThat(p0.getContent()).hasSize(2);
        assertThat(p1.getContent()).hasSize(1);
        assertThat(p1.getContent().get(0).getMessageText()).isEqualTo("a");
    }

    @Test
    void whenNoMessages_returnsEmptyPage() {
        ChatRoomEntity room = persistChatRoom("emptyRoom");
        Integer roomId = room.getId();

        Page<ChatMessageEntity> page = chatMessageRepository.findByChatRoomIdOrderByCreateDateDesc(
                roomId,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"))
        );
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();
    }
}
