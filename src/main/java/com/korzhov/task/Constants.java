package com.korzhov.task;

public class Constants {
    public static final int ZERO_TRANSACTIONS = 0;
    public static final int TRANSACTION_COUNT = 1;
    public static final long ZERO_EPOCH_SECONDS = 0L;

    public static class Message {
        public static final String TRANSACTION_EXPIRED = "Transaction is older than 60 seconds";
        public static final String STATISTIC_NOT_PREPARED = "Statistic array is not prepared";
        public static final String TRANSACTION_IN_FUTURE = "Transaction date is in the future";
    }
}
