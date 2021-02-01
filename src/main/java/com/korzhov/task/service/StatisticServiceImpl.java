package com.korzhov.task.service;

import com.korzhov.task.StatsBuffer;
import com.korzhov.task.domain.StatisticEntry;
import com.korzhov.task.domain.StatisticResponse;
import com.korzhov.task.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatsBuffer statsBuffer;

    @Autowired
    public StatisticServiceImpl(StatsBuffer statsBuffer) {
        this.statsBuffer = statsBuffer;
    }

    @Override
    public StatisticResponse getStatistic() {
        StatisticEntry fullStats = statsBuffer.getFullStats();
        StatisticResponse response = new StatisticResponse();
        response.setSum(fullStats.getSum());
        response.setAvg(fullStats.getAvg());
        response.setMax(fullStats.getMax());
        response.setMin(fullStats.getMin());
        response.setCount(fullStats.getCount());
        return response;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        statsBuffer.setStats(transaction);
        log.info("Transaction created");
        return transaction;
    }

    @Override
    public void deleteStatistic() {
        statsBuffer.clear();
        log.info("All transactions deleted");
    }

}
