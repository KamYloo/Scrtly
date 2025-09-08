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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
                .build();
        p.setLikes(new HashSet<>());
        p.setComments(new ArrayList<>());
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
                .build();
        c.setLikes(new HashSet<>());
        c.setReplies(new HashSet<>());
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
        if (post.getLikes() == null) post.setLikes(new HashSet<>());
        post.getLikes().add(l);
        em.merge(post);
        em.flush();
        return l;
    }

    private LikeEntity persistLikeForComment(UserEntity user, CommentEntity comment) {
        LikeEntity l = LikeEntity.builder()
                .user(user)
                .comment(comment)
                .build();
        em.persist(l);
        if (comment.getLikes() == null) comment.setLikes(new HashSet<>());
        comment.getLikes().add(l);
        em.merge(comment);
        em.flush();
        return l;
    }

    @Test
    void isLikeExistPost_returnsLikeWhenPresent_andNullWhenAbsent() {
        UserEntity u = persistUser("UserA");
        PostEntity p = persistPost("imgA.jpg", "descA", u);

        LikeEntity none = likeRepository.isLikeExistPost(u.getId(), p.getId());
        assertThat(none).isNull();

        LikeEntity saved = persistLikeForPost(u, p);

        LikeEntity found = likeRepository.isLikeExistPost(u.getId(), p.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getPost()).isNotNull();
        assertThat(found.getPost().getId()).isEqualTo(p.getId());
        assertThat(found.getUser()).isNotNull();
        assertThat(found.getUser().getId()).isEqualTo(u.getId());
    }

    @Test
    void findByPostId_returnsAllLikesForGivenPost() {
        UserEntity u1 = persistUser("Liker1");
        UserEntity u2 = persistUser("Liker2");
        UserEntity u3 = persistUser("Liker3");
        PostEntity post = persistPost("post.jpg", "post", persistUser("Author"));

        persistLikeForPost(u1, post);
        persistLikeForPost(u2, post);
        persistLikeForPost(u3, post);

        List<LikeEntity> likes = likeRepository.findByPostId(post.getId());
        assertThat(likes).hasSize(3);
        assertThat(likes).allSatisfy(l -> {
            assertThat(l.getPost()).isNotNull();
            assertThat(l.getPost().getId()).isEqualTo(post.getId());
            assertThat(l.getUser()).isNotNull();
        });
    }

    @Test
    void isLikeExistComment_and_findByCommentId_workForCommentLikes() {
        UserEntity author = persistUser("AuthorC");
        PostEntity post = persistPost("pC.jpg", "descC", author);
        CommentEntity c = persistComment(post, persistUser("Commenter"));

        assertThat(likeRepository.isLikeExistComment(author.getId(), c.getId())).isNull();

        LikeEntity saved = persistLikeForComment(author, c);

        LikeEntity found = likeRepository.isLikeExistComment(author.getId(), c.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());

        List<LikeEntity> likes = likeRepository.findByCommentId(c.getId());
        assertThat(likes).hasSize(1);
        assertThat(likes.get(0).getComment()).isNotNull();
        assertThat(likes.get(0).getComment().getId()).isEqualTo(c.getId());
    }
    }