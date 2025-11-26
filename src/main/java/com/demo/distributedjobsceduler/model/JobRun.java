package com.demo.distributedjobsceduler.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "job_runs")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class JobRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private Integer attempt;
    private String status; // RUNNING, SUCCEEDED, FAILED
    Instant createdAt;
    private Instant startedAt;
    private Instant finishedAt;

    @Column(columnDefinition = "text")
    private String output;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}
