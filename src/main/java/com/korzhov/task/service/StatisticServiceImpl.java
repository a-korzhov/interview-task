package com.korzhov.task.service;

import com.korzhov.task.exception.StatisticIsEmptyException;
import com.korzhov.task.exception.TransactionExpiredException;
import com.korzhov.task.model.StatisticEntry;
import com.korzhov.task.model.Transaction;
import com.korzhov.task.repository.StatisticBuffer;
import com.korzhov.task.util.TimesUtil;
import com.korzhov.task.web.StatisticResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import static com.korzhov.task.Constants.*;
import static com.korzhov.task.Constants.Message.TRANSACTION_EXPIRED;
import static com.korzhov.task.repository.StatisticBuffer.INDEX_COUNTER;
import static com.korzhov.task.util.StatsCounter.avg;
import static com.korzhov.task.util.StatsCounter.sum;
import static com.korzhov.task.util.TimesUtil.isExpired;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatisticBuffer statisticBuffer;
    private final ReentrantLock lock = new ReentrantLock();


    @Autowired
    public StatisticServiceImpl(StatisticBuffer statisticBuffer) {
        this.statisticBuffer = statisticBuffer;
    }

    @Override
    public StatisticResponse getStatistic() {
        if (Arrays.stream(statisticBuffer.getData()).allMatch(Objects::isNull)) {
            throw new StatisticIsEmptyException(Message.STATISTIC_NOT_PREPARED);
        }
        StatisticResponse response = new StatisticResponse();
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        BigDecimal min = BigDecimal.ZERO;
        int count = ZERO_TRANSACTIONS;

        StatisticEntry[] data = statisticBuffer.getData();

        // Count statistic from statistic buffer, ignoring expired and empty entries.
        for (int i = 0; i < data.length - 1; i++) {
            long statTimeInEpochSeconds = data[i].getTimestamp().toEpochSecond();
            if (!isExpired(data[i].getTimestamp().toLocalDateTime()) &&
                    statTimeInEpochSeconds > ZERO_EPOCH_SECONDS) {
                sum = sum(sum, data[i].getSum());
                count += data[i].getCount();
                max = data[i].getMax().max(data[i + 1].getMax());
                min = data[i].getMin().min(data[i + 1].getMin());
            }
        }
        response.setSum(sum);
        response.setCount(count);
        if (count == ZERO_TRANSACTIONS) {
            response.setAvg(BigDecimal.ZERO);
        } else {
            response.setAvg(avg(response.getSum(), response.getCount()));
        }
        response.setMax(max);
        response.setMin(min);
        return response;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        lock.lock();
        try {
            /* If array is not prepared(each cell is equal null), then add empty statistic objects to each cell.
            Set property isEmpty = true. That means that array is empty, but prepared.
            */
            if (statisticBuffer.hasOnlyNulls()) {
                statisticBuffer.prepareInMemoryDatabase();
            }
            if (isExpired(transaction.getTimestamp())) {
                throw new TransactionExpiredException(TRANSACTION_EXPIRED);
            }

            StatisticEntry currentEntry = statisticBuffer.getEntryByIndex(INDEX_COUNTER);

            StatisticEntry[] data = statisticBuffer.getData();
            BigDecimal tAmount = transaction.getAmount();
            if (TimesUtil.compareEpochSeconds(transaction, currentEntry)) {
                if (!statisticBuffer.isEmpty()) {
                    statisticBuffer.updateIndexCounter();
                }
                StatisticEntry newEntry = new StatisticEntry();
                newEntry.setSum(tAmount);
                newEntry.setCount(TRANSACTION_COUNT);
                newEntry.setMax(tAmount);
                newEntry.setMin(tAmount);
                newEntry.setTimestamp(transaction.getTimestamp());
                data[INDEX_COUNTER] = newEntry;
                statisticBuffer.setEmpty(false);
            } else {
                currentEntry.setSum(sum(currentEntry.getSum(), tAmount));
                currentEntry.setCount(currentEntry.getCount() + TRANSACTION_COUNT);
                currentEntry.setMax(currentEntry.getMax().max(tAmount));
                currentEntry.setMin(currentEntry.getMin().min(tAmount));
            }

            log.info("Array of stats per second: {}", Arrays.toString(data));
            log.info("Transaction created");
        } finally {
            lock.unlock();
        }
        return transaction;
    }

    @Override
    public void deleteStatistic() {
        statisticBuffer.clear();
        log.info("All transactions deleted");
    }

}
