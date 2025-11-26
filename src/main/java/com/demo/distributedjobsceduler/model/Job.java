package com.demo.distributedjobsceduler.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String cronExpression;
    String handlerType;
    @Column(columnDefinition = "text")
    String handlerData;
    String status; // ACTIVE, PAUSED, DELETED
    Integer retryCount;
    Integer retryBackoffSeconds;
    Instant nextRunAt;
    Instant createdAt;
    Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
