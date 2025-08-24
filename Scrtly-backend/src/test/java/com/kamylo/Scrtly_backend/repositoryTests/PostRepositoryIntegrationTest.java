package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.like.domain.LikeEntity;
import com.kamylo.Scrtly_backend.post.domain.PostEntity;
import com.kamylo.Scrtly_backend.post.repository.PostRepository;
import com.kamylo.Scrtly_backend.post.repository.PostSpecification;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String uniq = String.valueOf(System.nanoTime());
        UserEntity user = UserEntity.builder()
                .fullName(base + " Full")
                .nickName((base + "_" + uniq).toLowerCase())
                .email((base + uniq).toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        user.setFollowers(new HashSet<>());
        user.setFollowings(new HashSet<>());
        user.setRoles(new HashSet<>());
        user.setPostEntity(new ArrayList<>());
        em.persist(user);
        em.flush();
        return user;
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

    private void persistLike(UserEntity user, PostEntity post) {
        LikeEntity l = LikeEntity.builder()
                .user(user)
                .post(post)
                .build();
        em.persist(l);
        if (post.getLikes() == null) post.setLikes(new HashSet<>());
        post.getLikes().add(l);
        em.merge(post);
        em.flush();
    }

    @Test
    void findByUserId_returnsPagedResults_and_userLoaded() {
        UserEntity u1 = persistUser("Alice");
        UserEntity u2 = persistUser("Bob");

        persistPost("img1.jpg", "desc1", u1);
        persistPost("img2.jpg", "desc2", u1);
        persistPost("img3.jpg", "desc3", u1);
        persistPost("img_other.jpg", "descX", u2);

        Page<PostEntity> page = postRepository.findByUserId(u1.getId(), PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allSatisfy(p -> {
            assertThat(p.getUser()).isNotNull();
            assertThat(p.getUser().getId()).isEqualTo(u1.getId());
            assertThat(p.getUser().getNickName()).isEqualTo(u1.getNickName());
        });
    }

    @Test
    void findAll_withLikesSpecification_filtersByMinAndMaxLikes() {
        UserEntity author = persistUser("Author");
        UserEntity liker1 = persistUser("L1");
        UserEntity liker2 = persistUser("L2");
        UserEntity liker3 = persistUser("L3");

        PostEntity p0 = persistPost("p0.jpg", "zero likes", author); // 0
        PostEntity p1 = persistPost("p1.jpg", "one like", author);   // 1
        PostEntity p2 = persistPost("p2.jpg", "two likes", author);  // 2
        PostEntity p3 = persistPost("p3.jpg", "three likes", author);// 3

        persistLike(liker1, p1);

        persistLike(liker1, p2);
        persistLike(liker2, p2);

        persistLike(liker1, p3);
        persistLike(liker2, p3);
        persistLike(liker3, p3);

        Specification<PostEntity> specMin2 = PostSpecification.hasMinLikes(2);
        Page<PostEntity> min2 = postRepository.findAll(specMin2, PageRequest.of(0, 10));
        assertThat(min2.getTotalElements()).isEqualTo(2L); // p2 and p3

        Specification<PostEntity> between1and2 = Specification.where(PostSpecification.hasMinLikes(1))
                .and(PostSpecification.hasMaxLikes(2));
        Page<PostEntity> between = postRepository.findAll(between1and2, PageRequest.of(0, 10));
        assertThat(between.getTotalElements()).isEqualTo(2L); // p1 and p2

        between.getContent().forEach(p -> {
            assertThat(p.getUser()).isNotNull();
            assertThat(p.getUser().getId()).isEqualTo(author.getId());
        });
    }

    @Test
    void findAll_specificationReturnsEmptyWhenNoMatches() {
        UserEntity author = persistUser("Solo");
        persistPost("solo.jpg", "no likes", author);

        Specification<PostEntity> spec = Specification.where(PostSpecification.hasMinLikes(5));
        Page<PostEntity> page = postRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();
    }
}