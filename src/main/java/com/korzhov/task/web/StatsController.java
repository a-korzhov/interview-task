package com.korzhov.task.web;

import com.korzhov.task.exception.TransactionInFutureException;
import com.korzhov.task.model.Transaction;
import com.korzhov.task.service.StatisticService;
import com.korzhov.task.service.StatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static com.korzhov.task.Constants.Message.TRANSACTION_IN_FUTURE;

@RestController
@RequestMapping(value = "/api")
public class StatsController {
    private final StatisticService statisticsService;

    @Autowired
    public StatsController(StatisticServiceImpl statisticsService) {
        this.statisticsService = statisticsService;
    }

    /*
    POST /transactions
    This endpoint is called to create a new transaction.
    It MUST execute in constant time and memory (O(1)).
     */
    @PostMapping(value = "/transactions")
    public ResponseEntity<Transaction> create(@RequestBody @Valid Transaction transaction) {
        if (transaction.getTimestamp().isAfter(LocalDateTime.now())) {
            throw new TransactionInFutureException(TRANSACTION_IN_FUTURE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(statisticsService.saveTransaction(transaction));
    }


    /*
    GET /statistics
    This endpoint returns the statistics based on the transactions that happened in the last 60 seconds.
    It MUST execute in constant time and memory (O(1)).
     */
    @GetMapping(value = "/stats")
    public ResponseEntity<StatisticResponse> getStats() {
        return ResponseEntity.ok(statisticsService.getStatistic());
    }

    /*
    DELETE /transactions
    This endpoint causes all existing transactions to be deleted
    The endpoint should accept an empty request body and return a 204 status code.
     */
    @DeleteMapping(value = "/transactions")
    public ResponseEntity<Void> delete() {
        statisticsService.deleteStatistic();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
