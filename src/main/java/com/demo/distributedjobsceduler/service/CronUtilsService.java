package com.demo.distributedjobsceduler.service;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;

@Service
public class CronUtilsService {
    private final CronParser parser;

    public CronUtilsService() {
        parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING)); // 6-field (seconds)
    }

    public Instant nextExecution(String cronExpression, Instant from) {
        var cron = parser.parse(cronExpression);
        var executionTime = ExecutionTime.forCron(cron);
        var next = executionTime.nextExecution(from.atZone(ZoneId.systemDefault())).map(ChronoZonedDateTime::toInstant);
        return next.orElseThrow(() -> new IllegalArgumentException("Cannot compute next execution time"));
    }
}
