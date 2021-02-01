package com.korzhov.task.domain;

import lombok.Data;

import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private BigDecimal amount;
    @PastOrPresent(message = "Transaction cannot be in the future")
    private LocalDateTime timestamp;
}
