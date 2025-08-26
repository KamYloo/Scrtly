package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;
import com.kamylo.Scrtly_backend.comment.repository.CommentSpecification;
import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
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

    private CommentEntity persistComment(String text, PostEntity post, UserEntity user, CommentEntity parent, LocalDateTime created, LocalDateTime modified) {
        CommentEntity c = CommentEntity.builder()
                .comment(text)
                .creationDate(created)
                .lastModifiedDate(modified)
                .post(post)
                .user(user)
                .parentComment(parent)
                .build();
        c.setLikes(new HashSet<>());
        c.setReplies(new HashSet<>());

        em.persist(c);
        em.flush();
        return c;
    }

    private void persistLike(UserEntity user, CommentEntity comment) {
        LikeEntity l = LikeEntity.builder()
                .user(user)
                .comment(comment)
                .build();
        em.persist(l);
        if (comment.getLikes() == null) comment.setLikes(new HashSet<>());
        comment.getLikes().add(l);
        em.merge(comment);
        em.flush();
    }

    @Test
    void findByParentCommentId_returnsPagedChildren_and_loadsRelations() {
        UserEntity author = persistUser("AuthorParent");
        PostEntity post = persistPost("p.jpg", "desc", author);

        CommentEntity parent = persistComment("parent", post, author, null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        persistComment("c1", post, author, parent, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        persistComment("c2", post, author, parent, LocalDateTime.now().minusHours(20), LocalDateTime.now().minusHours(20));
        persistComment("c3", post, author, parent, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(10));
        CommentEntity otherParent = persistComment("otherParent", post, author, null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3));
        persistComment("otherChild", post, author, otherParent, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));

        Page<CommentEntity> page = commentRepository.findByParentCommentId(parent.getId(), PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);
        page.getContent().forEach(c -> {
            assertThat(c.getParentComment()).isNotNull();
            assertThat(c.getParentComment().getId()).isEqualTo(parent.getId());
            assertThat(c.getUser()).isNotNull();
            assertThat(c.getPost()).isNotNull();
        });
    }

    @Test
    void findAll_byPostId_filtersCommentsCorrectly() {
        UserEntity u = persistUser("Poster");
        PostEntity postA = persistPost("a.jpg", "a", u);
        PostEntity postB = persistPost("b.jpg", "b", u);

        persistComment("pA-c1", postA, u, null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        persistComment("pA-c2", postA, u, null, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        persistComment("pB-c1", postB, u, null, LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(5));

        Specification<CommentEntity> spec = CommentSpecification.byPostId(postA.getId());
        Page<CommentEntity> page = commentRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2L);
        page.getContent().forEach(c -> assertThat(c.getPost().getId()).isEqualTo(postA.getId()));
    }

    @Test
    void findAll_orderByLikes_sortsByLikesDescending() {
        UserEntity u = persistUser("LikerTest");
        PostEntity post = persistPost("likes.jpg", "likes", u);

        CommentEntity c0 = persistComment("zero", post, u, null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3));
        CommentEntity c1 = persistComment("one", post, u, null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2));
        CommentEntity c2 = persistComment("two", post, u, null, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));

        persistLike(persistUser("L1"), c2);
        persistLike(persistUser("L2"), c2);
        persistLike(persistUser("L3"), c1);

        Specification<CommentEntity> spec = CommentSpecification.orderByLikes();
        Page<CommentEntity> page = commentRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(3L);
        List<CommentEntity> ordered = page.getContent();
        assertThat(ordered.get(0).getId()).isEqualTo(c2.getId());
        assertThat(ordered.get(1).getId()).isEqualTo(c1.getId());
        assertThat(ordered.get(2).getId()).isEqualTo(c0.getId());
    }
}