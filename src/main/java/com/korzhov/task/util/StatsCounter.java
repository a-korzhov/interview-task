package com.korzhov.task.util;

import com.korzhov.task.StatsBuffer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatsCounter {

    public static BigDecimal sum(BigDecimal prevSum, BigDecimal amount) {
        return prevSum.add(amount);
    }

    public static BigDecimal avg(BigDecimal sum, int count) {
        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }


    public static BigDecimal max(BigDecimal n1, BigDecimal n2) {
        if (n1.compareTo(n2) >= 0) return n1;
        else return n2;
    }

    public static BigDecimal min(BigDecimal n1, BigDecimal n2) {
        if (n1.compareTo(n2) >= 0) return n2;
        else return n1;
    }
}
