package com.korzhov.task.exception;

public class TransactionInFutureException extends RuntimeException{
    public TransactionInFutureException(String message) {
        super(message);
    }

    public TransactionInFutureException(String message, Throwable cause) {
        super(message, cause);
    }
}
