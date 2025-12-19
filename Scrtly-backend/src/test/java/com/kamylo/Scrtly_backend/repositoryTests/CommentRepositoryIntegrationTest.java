package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.comment.repository.CommentRepository;
import com.kamylo.Scrtly_backend.comment.repository.CommentSpecification;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
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
                .likeCount(0)
                .commentCount(0)
                .build();
        em.persist(p);
        em.flush();
        return p;
    }

    private CommentEntity persistComment(String text, PostEntity post, UserEntity user, CommentEntity parent, int initialLikes) {
        CommentEntity c = CommentEntity.builder()
                .comment(text)
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .post(post)
                .user(user)
                .parentComment(parent)
                .likeCount(initialLikes)
                .build();
        c.setReplies(new HashSet<>());
        em.persist(c);
        em.flush();
        return c;
    }

    @Test
    void incrementLikeCount_increasesValueByOne() {
        UserEntity user = persistUser("User");
        PostEntity post = persistPost("img", "desc", user);
        CommentEntity comment = persistComment("text", post, user, null, 10);
        commentRepository.incrementLikeCount(comment.getId());

        em.clear();

        CommentEntity updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(11);
    }

    @Test
    void decrementLikeCount_decreasesValueByOne() {
        UserEntity user = persistUser("User");
        PostEntity post = persistPost("img", "desc", user);
        CommentEntity comment = persistComment("text", post, user, null, 10);
        commentRepository.decrementLikeCount(comment.getId());

        em.clear();

        CommentEntity updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(9);
    }

    @Test
    void findByParentCommentId_returnsPagedChildren_and_loadsRelations() {
        UserEntity author = persistUser("AuthorParent");
        PostEntity post = persistPost("p.jpg", "desc", author);

        CommentEntity parent = persistComment("parent", post, author, null, 0);
        persistComment("c1", post, author, parent, 0);
        persistComment("c2", post, author, parent, 0);

        CommentEntity otherParent = persistComment("otherParent", post, author, null, 0);
        persistComment("otherChild", post, author, otherParent, 0);

        Page<CommentEntity> page = commentRepository.findByParentCommentId(parent.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2L);
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

        persistComment("pA-c1", postA, u, null, 0);
        persistComment("pA-c2", postA, u, null, 0);
        persistComment("pB-c1", postB, u, null, 0);

        Specification<CommentEntity> spec = CommentSpecification.byPostId(postA.getId());
        Page<CommentEntity> page = commentRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2L);
        page.getContent().forEach(c -> assertThat(c.getPost().getId()).isEqualTo(postA.getId()));
    }

    @Test
    void findAll_orderByLikes_sortsByLikesDescending() {
        UserEntity u = persistUser("LikerTest");
        PostEntity post = persistPost("likes.jpg", "likes", u);

        CommentEntity c2 = persistComment("two", post, u, null, 100);
        CommentEntity c1 = persistComment("one", post, u, null, 50);
        CommentEntity c0 = persistComment("zero", post, u, null, 0);

        Specification<CommentEntity> spec = CommentSpecification.orderByLikes();
        Page<CommentEntity> page = commentRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(3L);
        List<CommentEntity> ordered = page.getContent();

        assertThat(ordered.get(0).getId()).isEqualTo(c2.getId());
        assertThat(ordered.get(0).getLikeCount()).isEqualTo(100);

        assertThat(ordered.get(1).getId()).isEqualTo(c1.getId());
        assertThat(ordered.get(1).getLikeCount()).isEqualTo(50);

        assertThat(ordered.get(2).getId()).isEqualTo(c0.getId());
        assertThat(ordered.get(2).getLikeCount()).isEqualTo(0);
    }
}