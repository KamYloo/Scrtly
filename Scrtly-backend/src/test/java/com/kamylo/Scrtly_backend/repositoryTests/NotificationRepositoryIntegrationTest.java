package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.notification.domain.NotificationEntity;
import com.kamylo.Scrtly_backend.notification.repository.NotificationRepository;
import com.kamylo.Scrtly_backend.notification.domain.enums.NotificationType;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String uniq = String.valueOf(System.nanoTime());
        UserEntity u = UserEntity.builder()
                .fullName(base + " Full")
                .nickName((base + "_" + uniq).toLowerCase())
                .email((base + uniq).toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(u);
        em.flush();
        return u;
    }

    private PostEntity persistPost(String image, String description, UserEntity user) {
        PostEntity p = PostEntity.builder()
                .image(image)
                .description(description)
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .user(user)
                .build();
        p.setLikes(new HashSet<>());
        p.setComments(new ArrayList<>());
        em.persist(p);
        em.flush();
        return p;
    }

    private NotificationEntity persistNotification(String message,
                                                   LocalDateTime created,
                                                   LocalDateTime updated,
                                                   NotificationType type,
                                                   UserEntity recipient,
                                                   PostEntity post) {
        NotificationEntity n = NotificationEntity.builder()
                .message(message)
                .seen(false)
                .createdDate(created)
                .updatedDate(updated)
                .type(type)
                .count(1)
                .recipient(recipient)
                .post(post)
                .build();
        em.persist(n);
        em.flush();
        return n;
    }

    @Test
    void findByRecipientAndTypeAndPost_returnsWhenExists() {
        UserEntity recipient = persistUser("Recipient");
        UserEntity author = persistUser("Author");
        PostEntity post = persistPost("p.jpg", "desc", author);

        NotificationType type = NotificationType.values()[0];

        NotificationEntity saved = persistNotification("hello",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(10),
                type, recipient, post);

        Optional<NotificationEntity> found = notificationRepository.findByRecipientAndTypeAndPost(recipient, type, post);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getRecipient()).isNotNull();
        assertThat(found.get().getRecipient().getId()).isEqualTo(recipient.getId());
        assertThat(found.get().getPost()).isNotNull();
        assertThat(found.get().getPost().getId()).isEqualTo(post.getId());
    }

    @Test
    void findByRecipientIdOrderByUpdatedDateDescCreatedDateDesc_pagesAnd_loadsRecipientAndPost() {
        UserEntity recipient = persistUser("NotifUser");
        UserEntity author = persistUser("Author2");
        PostEntity postA = persistPost("a.jpg", "a", author);
        PostEntity postB = persistPost("b.jpg", "b", author);

        NotificationType type = NotificationType.values()[0];

        persistNotification("n1",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                type, recipient, postA);
        persistNotification("n2",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(6),
                type, recipient, null);
        persistNotification("n3",
                LocalDateTime.now().minusHours(10),
                LocalDateTime.now().minusHours(1),
                type, recipient, postB);

        Page<NotificationEntity> page = notificationRepository.findByRecipientIdOrderByUpdatedDateDescCreatedDateDesc(recipient.getId(), PageRequest.of(0, 2));
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);

        assertThat(page.getContent().get(0).getUpdatedDate()).isAfterOrEqualTo(page.getContent().get(1).getUpdatedDate());

        page.getContent().forEach(n -> {
            assertThat(n.getRecipient()).isNotNull();
            assertThat(n.getRecipient().getId()).isEqualTo(recipient.getId());
        });
    }

    @Test
    void deleteAllByPost_removesOnlyNotificationsForProvidedPost() {
        UserEntity recipient = persistUser("DelUser");
        UserEntity author = persistUser("Author3");
        PostEntity post1 = persistPost("one.jpg", "one", author);
        PostEntity post2 = persistPost("two.jpg", "two", author);

        NotificationType type = NotificationType.values()[0];

        persistNotification("for1a", LocalDateTime.now(), LocalDateTime.now(), type, recipient, post1);
        persistNotification("for1b", LocalDateTime.now(), LocalDateTime.now(), type, recipient, post1);
        persistNotification("for2", LocalDateTime.now(), LocalDateTime.now(), type, recipient, post2);

        notificationRepository.deleteAllByPost(post1);
        em.flush();
        em.clear();

        Page<NotificationEntity> remaining = notificationRepository.findByRecipientIdOrderByUpdatedDateDescCreatedDateDesc(recipient.getId(), PageRequest.of(0, 10));
        assertThat(remaining.getTotalElements()).isEqualTo(1L);
        assertThat(remaining.getContent().get(0).getPost()).isNotNull();
    }
}