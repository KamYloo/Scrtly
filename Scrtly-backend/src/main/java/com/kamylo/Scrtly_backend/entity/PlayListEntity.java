package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "playlist")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"songs", "user"})
public class PlayListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 255, message = "Cover image URL must be at most 255 characters")
    private String coverImage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    private boolean favourite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "playlist_songs",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id",
                    foreignKey = @ForeignKey(
                            name = "FK_playlist_songs_song",
                            foreignKeyDefinition =
                                    "FOREIGN KEY(song_id) REFERENCES songs(id) ON DELETE CASCADE"
                    ))
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SongEntity> songs = new HashSet<>();
}
