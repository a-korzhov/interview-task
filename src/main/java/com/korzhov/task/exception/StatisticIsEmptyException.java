package com.korzhov.task.exception;

public class StatisticIsEmptyException extends RuntimeException{
    public StatisticIsEmptyException(String message) {
        super(message);
    }

    public StatisticIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
