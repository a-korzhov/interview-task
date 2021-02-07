package com.korzhov.task.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
public class StatisticEntry {
    private BigDecimal sum;
    private BigDecimal max;
    private BigDecimal min;
    private int count;
    private ZonedDateTime timestamp;

    public StatisticEntry(BigDecimal sum, BigDecimal max, BigDecimal min, int count, LocalDateTime timestamp) {
        this.sum = sum;
        this.max = max;
        this.min = min;
        this.count = count;
        this.timestamp = timestamp.atZone(ZoneOffset.UTC);
    }

    public StatisticEntry() {
    }

    // All BigDecimal values always contain exactly two decimal places and use `HALF_ROUND_UP` rounding
    public BigDecimal getSum() {
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMax() {
        return max.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMin() {
        return min.setScale(2, RoundingMode.HALF_UP);
    }


    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.atZone(ZoneOffset.UTC);
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}