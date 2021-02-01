package com.korzhov.task;


import com.korzhov.task.domain.StatisticEntry;
import com.korzhov.task.domain.Transaction;
import com.korzhov.task.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import static com.korzhov.task.util.StatsCounter.*;

@Component
@Slf4j
public class StatsBuffer {
    // Assume, that we keep statistics of transactions in array per 1 second.
    // 60 Seconds = 60 statistics are inserted to the array.
    private static final int CAPACITY = 60;

    // Array of statistics
    private final StatisticEntry[] data;

    // Index counter that keeps info about index of current stats element in array
    private static int INDEX_COUNTER = 0;

    // The result of all statistic entries.
    private StatisticEntry fullStats;

    private final ReentrantLock lock = new ReentrantLock();

    public StatsBuffer() {
        this.data = new StatisticEntry[CAPACITY];
        this.fullStats = new StatisticEntry();
    }

    public void setStats(Transaction t) {
        lock.lock();
        try {
            if (isExpired(t)) {
                throw new TransactionException("Transaction is older than 60 seconds");
            }

            BigDecimal amount = t.getAmount();
        /* If the array is completely filled
           find the oldest index in the array.
           e.g current INDEX_COUNTER is 35. Then oldest INDEX_COUNTER must be 36.
           If current INDEX_COUNTER is 59 - oldest is 0.
         */
            if (isFull()) {
                int oldestElementIndex;
                if (INDEX_COUNTER == (CAPACITY - 1)) {
                    oldestElementIndex = 0;
                } else {
                    oldestElementIndex = INDEX_COUNTER + 1;
                }
                // Update statistic deleting old statistic by oldest index entry in array.
                BigDecimal updated = fullStats.getSum().subtract(data[oldestElementIndex].getSum());
                fullStats.setSum(updated);
            }

            // if it is first entry in the array
            if (INDEX_COUNTER == 0 && data[INDEX_COUNTER] == null) {
                // filling stats
                StatisticEntry s = fillStats(t, amount);
                fullStats.setSum(s.getSum());
                fullStats.setCount(1);
                fullStats.setAvg(s.getAvg());
                fullStats.setMax(s.getMax());
                fullStats.setMin(s.getMin());
                fullStats.setTimestamp(s.getTimestamp().toLocalDateTime());
            /* if last statistic entry has the same timestamp with current transaction,
            then add amount to the same statistic entry.
             */
            } else if (data[INDEX_COUNTER].getTimestamp().equals(t.getTimestamp())) {
                StatisticEntry s = data[INDEX_COUNTER];
                //filling stats
                s.setSum(sum(s.getSum(), amount));
                s.setCount(s.getCount() + 1);
                s.setAvg(avg(s.getSum(), s.getCount()));
                s.setMax(max(s.getMax(), amount));
                s.setMin(min(s.getMin(), amount));
                fullStats.setSum(sum(fullStats.getSum(), amount));
                fullStats.setCount(s.getCount());
                fullStats.setAvg(avg(fullStats.getSum(), fullStats.getCount()));
                fullStats.setMax(s.getMax());
                fullStats.setMin(s.getMin());
                fullStats.setTimestamp(s.getTimestamp().toLocalDateTime());
                // if timestamps are different - increment INDEX_COUNTER and create new statistic
            } else if (!data[INDEX_COUNTER].getTimestamp().equals(t.getTimestamp())) {
                setLastEntryIndex(); // increment INDEX for next element in array.
                StatisticEntry s = fillStats(t, amount);
                // filling stats
                fullStats.setSum(sum(fullStats.getSum(), amount));
                fullStats.setCount(fullStats.getCount() + 1);
                fullStats.setAvg(avg(fullStats.getSum(), fullStats.getCount()));
                fullStats.setMax(max(fullStats.getMax(), amount));
                fullStats.setMin(min(fullStats.getMin(), amount));
                fullStats.setTimestamp(s.getTimestamp().toLocalDateTime());
            }
            log.info("Array of stats per second: {}", Arrays.toString(data));
        } finally {
            lock.unlock();
        }
    }

    public boolean isExpired(Transaction transaction) {
        return Duration.between(transaction.getTimestamp(), LocalDateTime.now())
                .compareTo(Duration.ofMinutes(1)) > 0;
    }

    /* Method to check if current INDEX_COUNTER is equal to last index in array, then reset INDEX_COUNTER.
       Otherwise increment INDEX_COUNTER
     */
    public void setLastEntryIndex() {
        if (INDEX_COUNTER == data.length - 1) {
            INDEX_COUNTER = 0;
        } else {
            INDEX_COUNTER++;
        }
    }

    public boolean isFull() {
        boolean hasNoNulls = false;
        for (int i = 0; i < data.length; i++) {
            hasNoNulls = data[i] != null;
        }
        return hasNoNulls;
    }

    public void clear() {
        fullStats.setSum(BigDecimal.ZERO);
        fullStats.setAvg(BigDecimal.ZERO);
        fullStats.setMax(BigDecimal.ZERO);
        fullStats.setMin(BigDecimal.ZERO);
        fullStats.setCount(0);
        fullStats.setTimestamp(null);
    }

    private StatisticEntry fillStats(Transaction t, BigDecimal amount) {
        StatisticEntry s = new StatisticEntry();
        s.setSum(amount);
        s.setAvg(amount);
        s.setMax(amount);
        s.setMin(amount);
        s.setCount(1);
        s.setTimestamp(t.getTimestamp());
        data[INDEX_COUNTER] = s;
        return s;
    }

    public StatisticEntry[] getData() {
        return data;
    }

    public StatisticEntry getFullStats() {
        return fullStats;
    }

    public void setFullStats(StatisticEntry fullStats) {
        this.fullStats = fullStats;
    }
}
