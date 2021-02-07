package com.korzhov.task.service;

import com.korzhov.task.model.Transaction;
import com.korzhov.task.web.StatisticResponse;

public interface StatisticService {
    StatisticResponse getStatistic();

    Transaction createTransaction(Transaction t);

    void deleteStatistic();
}
