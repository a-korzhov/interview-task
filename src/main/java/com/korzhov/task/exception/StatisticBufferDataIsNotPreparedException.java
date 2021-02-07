package com.korzhov.task.exception;

public class StatisticBufferDataIsNotPreparedException extends RuntimeException{
    public StatisticBufferDataIsNotPreparedException(String message) {
        super(message);
    }

    public StatisticBufferDataIsNotPreparedException(String message, Throwable cause) {
        super(message, cause);
    }
}
