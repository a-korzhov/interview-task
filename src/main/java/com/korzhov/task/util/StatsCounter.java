package com.korzhov.task.util;

import com.korzhov.task.model.StatisticEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class StatsCounter {

    public static BigDecimal sum(BigDecimal prevSum, BigDecimal amount) {
        return prevSum.add(amount);
    }

    public static BigDecimal avg(BigDecimal sum, int count) {
        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal sumFromList(List<StatisticEntry> list) {
        return list.stream()
                .map(StatisticEntry::getSum)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public static BigDecimal maxFromList(List<StatisticEntry> list) {
        return list.stream()
                .map(StatisticEntry::getMax)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public static BigDecimal minFromList(List<StatisticEntry> list) {
        return list.stream()
                .map(StatisticEntry::getMin)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public static int countFromList(List<StatisticEntry> list) {
        return list.stream()
                .map(StatisticEntry::getCount)
                .reduce(Integer::sum).orElse(0);
    }

}
