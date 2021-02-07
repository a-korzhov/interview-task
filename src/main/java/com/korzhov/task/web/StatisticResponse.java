package com.korzhov.task.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private int count;

    public BigDecimal getSum() {
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAvg() {
        return avg.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMax() {
        return max.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMin() {
        return min.setScale(2, RoundingMode.HALF_UP);
    }
}
