package com.demo.distributedjobsceduler.repository;

import com.demo.distributedjobsceduler.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByNextRunAtBeforeAndStatus(Instant time, String status);
}
