package com.korzhov.task.exception;

public class TransactionExpiredException extends RuntimeException{
    public TransactionExpiredException(String message) {
        super(message);
    }

    public TransactionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
