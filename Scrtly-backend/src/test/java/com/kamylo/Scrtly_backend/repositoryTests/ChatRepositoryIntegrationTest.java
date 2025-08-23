package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.chat.domain.ChatRoomEntity;
import com.kamylo.Scrtly_backend.chat.repository.ChatRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChatRepositoryIntegrationTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll();
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

    private ChatRoomEntity persistChatRoom(String baseName, UserEntity... participants) {
        String roomName = baseName + "-" + UUID.randomUUID().toString().substring(0, 6);
        ChatRoomEntity room = ChatRoomEntity.builder()
                .chatRoomName(roomName)
                .createdAt(LocalDateTime.now())
                .build();

        if (participants != null) {
            for (UserEntity u : participants) {
                room.getParticipants().add(u);
            }
        }

        em.persist(room);
        em.flush();
        return room;
    }


    @Test
    void findChatRoomsByUserId_returnsRoomsForUser() {
        UserEntity alice = persistUser("alice");
        UserEntity bob = persistUser("bob");

        ChatRoomEntity room1 = persistChatRoom("room-alpha", alice, bob);
        ChatRoomEntity room3 = persistChatRoom("room-empty");

        em.flush();

        Long aliceId = alice.getId();
        List<ChatRoomEntity> aliceRooms = chatRepository.findChatRoomsByUserId(aliceId);

        assertThat(aliceRooms).isNotNull();
        assertThat(aliceRooms).extracting(ChatRoomEntity::getId).contains(room1.getId());
        assertThat(aliceRooms).noneMatch(r -> r.getId().equals(room3.getId()));
        assertThat(aliceRooms).anyMatch(r -> r.getParticipants().stream().anyMatch(u -> (u.getId()).equals(aliceId)));
    }

    @Test
    void findChatRoomsByUserId_returnsEmptyWhenUserHasNoRooms() {
        UserEntity lonely = persistUser("lonely");
        UserEntity other = persistUser("other");
        persistChatRoom("r1", other);
        persistChatRoom("r2");

        em.flush();

        Long lonerId = lonely.getId();
        List<ChatRoomEntity> result = chatRepository.findChatRoomsByUserId(lonerId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findChatRoomsByUserId_returnsAllRoomsForUser() {
        UserEntity u = persistUser("multi");
        ChatRoomEntity a = persistChatRoom("multi-1", u);
        ChatRoomEntity b = persistChatRoom("multi-2", u);
        ChatRoomEntity c = persistChatRoom("multi-3", u);

        em.flush();

        Long uid = u.getId();
        List<ChatRoomEntity> rooms = chatRepository.findChatRoomsByUserId(uid);

        assertThat(rooms).isNotNull();
        assertThat(rooms).extracting(ChatRoomEntity::getId).contains(a.getId(), b.getId(), c.getId());
        assertThat(rooms).allMatch(r -> r.getParticipants().stream().anyMatch(p -> p.getId().equals(uid)));
    }
}
