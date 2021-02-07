package com.korzhov.task.util;

import com.korzhov.task.model.StatisticEntry;
import com.korzhov.task.model.Transaction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimesUtil {
    private static final long LAST_MINUTE = 1;

    // returns false if epoch seconds are not equals
    public static boolean compareEpochSeconds(Transaction t, StatisticEntry s) {
        return t.getTimestamp().toEpochSecond(ZoneOffset.UTC) == s.getTimestamp().toEpochSecond();
    }

    // returns true if timestamp is expired
    public static boolean isExpired(LocalDateTime timestamp) {
        return Duration.between(timestamp, LocalDateTime.now())
                .compareTo(Duration.ofMinutes(LAST_MINUTE)) > 0;
    }
}
