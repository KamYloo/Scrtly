package com.kamylo.Scrtly_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatRoom", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"first_person_id", "second_person_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Data

public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "first_person_id")
    private User firstPerson;

    @ManyToOne
    @JoinColumn(name = "second_person_id")
    private User secondPerson;


    @OneToMany
    private List<ChatMessage> messages = new ArrayList<>();
}
