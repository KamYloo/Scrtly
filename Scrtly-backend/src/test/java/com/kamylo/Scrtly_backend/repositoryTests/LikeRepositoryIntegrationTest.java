package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.comment.domain.CommentEntity;
import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.like.repository.LikeRepository;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LikeRepositoryIntegrationTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
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

    private CommentEntity persistComment(PostEntity post, UserEntity user) {
        CommentEntity c = CommentEntity.builder()
                .comment("nice")
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .post(post)
                .user(user)
                .likeCount(0)
                .build();
        em.persist(c);
        em.flush();
        return c;
    }

    private LikeEntity persistLikeForPost(UserEntity user, PostEntity post) {
        LikeEntity l = LikeEntity.builder()
                .user(user)
                .post(post)
                .build();
        em.persist(l);
        em.flush();
        return l;
    }

    private LikeEntity persistLikeForComment(UserEntity user, CommentEntity comment) {
        LikeEntity l = LikeEntity.builder()
                .user(user)
                .comment(comment)
                .build();
        em.persist(l);
        em.flush();
        return l;
    }

    @Test
    void findByUserIdAndPostId_returnsOptionalWithLike_whenPresent() {
        UserEntity u = persistUser("UserA");
        PostEntity p = persistPost("imgA.jpg", "descA", u);

        Optional<LikeEntity> absent = likeRepository.findByUserIdAndPostId(u.getId(), p.getId());
        assertThat(absent).isEmpty();

        LikeEntity savedLike = persistLikeForPost(u, p);

        Optional<LikeEntity> present = likeRepository.findByUserIdAndPostId(u.getId(), p.getId());
        assertThat(present).isPresent();
        assertThat(present.get().getId()).isEqualTo(savedLike.getId());
        assertThat(present.get().getPost().getId()).isEqualTo(p.getId());
    }

    @Test
    void findByUserIdAndCommentId_returnsOptionalWithLike_whenPresent() {
        UserEntity author = persistUser("AuthorC");
        PostEntity post = persistPost("pC.jpg", "descC", author);
        CommentEntity c = persistComment(post, persistUser("Commenter"));

        Optional<LikeEntity> absent = likeRepository.findByUserIdAndCommentId(author.getId(), c.getId());
        assertThat(absent).isEmpty();

        LikeEntity savedLike = persistLikeForComment(author, c);

        Optional<LikeEntity> present = likeRepository.findByUserIdAndCommentId(author.getId(), c.getId());
        assertThat(present).isPresent();
        assertThat(present.get().getId()).isEqualTo(savedLike.getId());
    }

    @Test
    void findPostIdsLikedByUser_returnsCorrectIds() {
        UserEntity user = persistUser("Liker");
        UserEntity otherUser = persistUser("Other");

        PostEntity p1 = persistPost("1.jpg", "1", user);
        PostEntity p2 = persistPost("2.jpg", "2", user);
        PostEntity p3 = persistPost("3.jpg", "3", user);

        persistLikeForPost(user, p1);
        persistLikeForPost(user, p3);

        persistLikeForPost(otherUser, p2);

        List<Long> postIdsToCheck = List.of(p1.getId(), p2.getId(), p3.getId());

        Set<Long> result = likeRepository.findPostIdsLikedByUser(user.getId(), postIdsToCheck);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(p1.getId(), p3.getId());
        assertThat(result).doesNotContain(p2.getId());
    }

    @Test
    void findCommentIdsLikedByUser_returnsCorrectIds() {
        UserEntity user = persistUser("CommentLiker");
        PostEntity post = persistPost("p.jpg", "d", user);

        CommentEntity c1 = persistComment(post, user);
        CommentEntity c2 = persistComment(post, user);
        CommentEntity c3 = persistComment(post, user);

        persistLikeForComment(user, c2);

        List<Long> commentIdsToCheck = List.of(c1.getId(), c2.getId(), c3.getId());

        Set<Long> result = likeRepository.findCommentIdsLikedByUser(user.getId(), commentIdsToCheck);

        assertThat(result).hasSize(1);
        assertThat(result).contains(c2.getId());
        assertThat(result).doesNotContain(c1.getId(), c3.getId());
    }
}