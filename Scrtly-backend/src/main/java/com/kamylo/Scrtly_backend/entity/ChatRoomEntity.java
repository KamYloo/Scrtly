package com.kamylo.Scrtly_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( uniqueConstraints = {
        @UniqueConstraint(columnNames = {"first_person_id", "second_person_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "first_person_id")
    @ToString.Exclude
    private UserEntity firstPerson;

    @ManyToOne
    @JoinColumn(name = "second_person_id")
    @ToString.Exclude
    private UserEntity secondPerson;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageEntity> messages = new ArrayList<>();
}
