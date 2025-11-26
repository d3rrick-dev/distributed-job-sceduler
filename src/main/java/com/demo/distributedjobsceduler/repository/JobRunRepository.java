package com.demo.distributedjobsceduler.repository;

import com.demo.distributedjobsceduler.model.JobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, Long> {}
