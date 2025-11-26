package com.demo.distributedjobsceduler.service;

import com.demo.distributedjobsceduler.model.Job;
import com.demo.distributedjobsceduler.model.JobRun;
import com.demo.distributedjobsceduler.repository.JobRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutorService {
    private final JobRunRepository jobRunRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public JobRun executeJob(Job job, int attempt) {
        log.info("Executing job id: {}, name: {}", job.getId(), job.getName());
        var jobRun = JobRun.builder()
                .jobId(job.getId())
                .attempt(attempt)
                .status("RUNNING") // (use enum in real app)
                .startedAt(Instant.now())
                .build();
        var savedJob = jobRunRepository.save(jobRun);

        try {
            String output = "no-op";
            if ("http".equalsIgnoreCase(job.getHandlerType())) {
                output = restTemplate.postForObject(job.getHandlerData(), null, String.class);
            } else if ("internal".equalsIgnoreCase(job.getHandlerType())) {
                output = "internal-executed:" + job.getHandlerData();
            } else {
                output = "unknown-handler";
            }

            var updatedJob = savedJob.toBuilder()
                    .output(output)
                    .status("SUCCEEDED") // (use enum in real app)
                    .finishedAt(Instant.now())
                    .build();
            jobRunRepository.save(updatedJob);
            return updatedJob;
        } catch (Exception e) {
            log.error("Job execution failed for jobId={} : {}", job.getId(), e.getMessage());
            var failedJob = savedJob.toBuilder()
                    .status("FAILED") // (use enum in real app)
                    .finishedAt(Instant.now())
                    .output(e.toString())
                    .build();
            jobRunRepository.save(failedJob);
            return failedJob;
        }
    }
}
