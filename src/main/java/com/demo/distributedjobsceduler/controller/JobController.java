package com.demo.distributedjobsceduler.controller;

import com.demo.distributedjobsceduler.model.Job;
import com.demo.distributedjobsceduler.model.JobRun;
import com.demo.distributedjobsceduler.repository.JobRepository;
import com.demo.distributedjobsceduler.service.CronUtilsService;
import com.demo.distributedjobsceduler.service.JobExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobRepository jobRepository; // use service on controller in real app
    private final JobExecutorService executor;
    private final CronUtilsService cronUtils;

    @PostMapping
    public ResponseEntity<Job> create(@RequestBody Job job) { // seperate DTO's in real app
        var nextRun = cronUtils.nextExecution(job.getCronExpression(), Instant.now());
        job.setNextRunAt(nextRun);
        Job saved = jobRepository.save(job);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Job> list() {
        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> get(@PathVariable Long id) {
        return jobRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //  useful for testing or admin
    @PostMapping("/{id}/run-now")
    public ResponseEntity<JobRun> runNow(@PathVariable Long id) {
        return jobRepository.findById(id).map(job -> {
            JobRun run = executor.executeJob(job, 1);
            return ResponseEntity.ok(run);
        }).orElse(ResponseEntity.notFound().build());
    }
}