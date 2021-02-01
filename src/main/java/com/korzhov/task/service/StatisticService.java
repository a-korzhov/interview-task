package com.korzhov.task.service;

import com.korzhov.task.domain.StatisticResponse;
import com.korzhov.task.domain.Transaction;

public interface StatisticService {
    StatisticResponse getStatistic();
    Transaction createTransaction(Transaction t);
    void deleteStatistic();
}
