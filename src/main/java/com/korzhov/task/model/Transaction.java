package com.korzhov.task.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
