package com.projects.nexigntest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.nexigntest.commons.CallType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cdr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private CallType callType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "caller_id", nullable = false)
    private Subscriber caller;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Subscriber receiver;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
