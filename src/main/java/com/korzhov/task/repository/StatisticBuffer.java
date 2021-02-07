package com.korzhov.task.repository;


import com.korzhov.task.model.StatisticEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.korzhov.task.Constants.ZERO_TRANSACTIONS;

@Component
@Slf4j
public class StatisticBuffer {
    // Assume that MAX capacity of buffer is 1000 statistic entries per 60 seconds.
    private static final int CAPACITY = 1000;

    // Index counter that keeps info about index of current statistic entry in array
    public static int INDEX_COUNTER = 0;

    // Array of statistics
    private final StatisticEntry[] data;

    private boolean isEmpty;

    public StatisticBuffer() {
        this.data = new StatisticEntry[CAPACITY];
    }

    public void clearOrPrepareInMemoryDatabase() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new StatisticEntry(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    ZERO_TRANSACTIONS,
                    LocalDateTime.MIN
            );
        }
        this.isEmpty = true;
    }

    public boolean hasOnlyNulls() {
        boolean isNull = false;
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] == null) {
                isNull = true;
            }
        }
        return isNull;
    }

    public void updateIndexCounter() {
        if (INDEX_COUNTER == data.length - 1) {
            INDEX_COUNTER = 0;
        } else {
            INDEX_COUNTER++;
        }
    }

    public StatisticEntry getEntryByIndex(int index) {
        return data[index];
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public StatisticEntry[] getData() {
        return data;
    }

}
