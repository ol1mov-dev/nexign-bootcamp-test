package com.projects.nexigntest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;

    @OneToMany(mappedBy = "caller")
    private List<Cdr> outgoingCalls; // Исходящие вызовы

    @OneToMany(mappedBy = "receiver")
    private List<Cdr> incomingCalls; // Входящие вызовы
}
