package com.kamylo.Scrtly_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( uniqueConstraints = {
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
    @ToString.Exclude
    private User firstPerson;

    @ManyToOne
    @JoinColumn(name = "second_person_id")
    @ToString.Exclude
    private User secondPerson;

    @JsonIgnore
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ChatMessage> messages = new ArrayList<>();
}
