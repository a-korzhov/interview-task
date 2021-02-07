package com.korzhov.task.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatsCounter {

    public static BigDecimal sum(BigDecimal prevSum, BigDecimal amount) {
        return prevSum.add(amount);
    }

    public static BigDecimal avg(BigDecimal sum, int count) {
        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
