package com.demo.distributedjobsceduler.service;

import com.demo.distributedjobsceduler.redis.RedisLockService;
import com.demo.distributedjobsceduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkerScheduler {
    private final JobRepository jobRepository;
    private final RedisLockService redisLockService;
    private final CronUtilsService cronUtilsService;
    private final JobExecutorService jobExecutorService;

    @Value("${scheduler.lock.expire.millis:10000}")
    private long lockExpireMillis;

    @Scheduled(fixedDelayString = "${scheduler.poll-interval-seconds:15}")
    public void pollAndRun() {
        var now = Instant.now();
        var jobsDue = jobRepository.findByNextRunAtBeforeAndStatus(now, "ACTIVE");
        jobsDue.forEach(job -> {
            var lockKey = "job-lock:" + job.getId();
            var lockToken = redisLockService.tryLock(lockKey, lockExpireMillis);
            if (lockToken != null) {
                try {
                    // Execute job
                    int attempt = 1;
                    jobExecutorService.executeJob(job, attempt);
                    // Update next run time for cron jobs
                    if (job.getCronExpression() != null && !job.getCronExpression().isBlank()) {
                        var nextRun = cronUtilsService.nextExecution(job.getCronExpression(), now);
                        job.setNextRunAt(nextRun);
                        jobRepository.save(job);
                    } else {
                        // For one-off jobs, deactivate after run
                        job.setStatus("INACTIVE");
                        jobRepository.save(job);
                    }
                } finally {
                    redisLockService.releaseLock(lockKey, lockToken);
                }
            }
        });
    }
}
