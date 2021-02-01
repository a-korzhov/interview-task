package com.korzhov.task.service;

import com.korzhov.task.StatsBuffer;
import com.korzhov.task.domain.StatisticEntry;
import com.korzhov.task.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticService {

    private final StatsBuffer statsBuffer;

    @Autowired
    public StatisticService(StatsBuffer statsBuffer) {
        this.statsBuffer = statsBuffer;
    }

    public StatisticEntry getStats() {
        return statsBuffer.getFullStats();
    }

    public Transaction createStats(Transaction transaction) {
        log.info("Transaction created");
        statsBuffer.setStats(transaction);
        return transaction;
    }

    public void deleteStats() {
        log.info("All transactions deleted");
        statsBuffer.clear();
    }


}
