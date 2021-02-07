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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.korzhov.task.Constants.Message.STATISTIC_NOT_PREPARED;
import static com.korzhov.task.Constants.Message.TRANSACTION_EXPIRED;
import static com.korzhov.task.Constants.*;
import static com.korzhov.task.repository.StatisticBuffer.INDEX_COUNTER;
import static com.korzhov.task.util.StatsCounter.*;
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
    public Transaction saveTransaction(Transaction transaction) {
        BigDecimal tAmount = transaction.getAmount();

        lock.lock();
        try {
            /* If array is not prepared(each cell is equal null), then add empty statistic objects to each cell.
            Set property isEmpty = true. That means that array is empty, but prepared.
            */
            if (statisticBuffer.hasOnlyNulls()) {
                statisticBuffer.clearOrPrepareInMemoryDatabase();
            }
            if (isExpired(transaction.getTimestamp())) {
                throw new TransactionExpiredException(TRANSACTION_EXPIRED);
            }

            StatisticEntry currentEntry = statisticBuffer.getEntryByIndex(INDEX_COUNTER);

            // If transaction timestamp is not equal to current statistic entry - create new entry
            if (!TimesUtil.compareEpochSeconds(transaction, currentEntry)) {
                if (!statisticBuffer.isEmpty()) {
                    statisticBuffer.updateIndexCounter();
                }
                StatisticEntry newEntry = new StatisticEntry(
                        tAmount, tAmount, tAmount, TRANSACTION_COUNT, transaction.getTimestamp());
                statisticBuffer.getData()[INDEX_COUNTER] = newEntry;
                statisticBuffer.setEmpty(false);
                // else update current statistic entry
            } else {
                currentEntry.setSum(sum(currentEntry.getSum(), tAmount));
                currentEntry.setCount(currentEntry.getCount() + TRANSACTION_COUNT);
                currentEntry.setMax(currentEntry.getMax().max(tAmount));
                currentEntry.setMin(currentEntry.getMin().min(tAmount));
            }
            log.info("Transaction saved");
        } finally {
            lock.unlock();
        }
        return transaction;
    }

    @Override
    public StatisticResponse getStatistic() {
        if (Arrays.stream(statisticBuffer.getData()).allMatch(Objects::isNull)) {
            throw new StatisticIsEmptyException(STATISTIC_NOT_PREPARED);
        }
        StatisticResponse response = new StatisticResponse();
        StatisticEntry[] data = statisticBuffer.getData();

        List<StatisticEntry> actualStatisticEntries = Arrays.stream(data)
                // get actual statistics, ignoring expired and empty data.
                .filter(s -> !isExpired(s.getTimestamp().toLocalDateTime()) && s.getTimestamp().toEpochSecond() > ZERO_EPOCH_SECONDS)
                .collect(Collectors.toList());

        response.setSum(sumFromList(actualStatisticEntries));
        int count = countFromList(actualStatisticEntries);
        response.setCount(count);
        if (count == ZERO_TRANSACTIONS) {
            response.setAvg(BigDecimal.ZERO);
        } else {
            response.setAvg(avg(response.getSum(), response.getCount()));
        }
        response.setMax(maxFromList(actualStatisticEntries));
        response.setMin(minFromList(actualStatisticEntries));

        return response;
    }

    @Override
    public void deleteStatistic() {
        statisticBuffer.clearOrPrepareInMemoryDatabase();
        log.info("All transactions deleted");
    }
}
