package com.korzhov.task.web;

import com.korzhov.task.domain.StatisticResponse;
import com.korzhov.task.domain.Transaction;
import com.korzhov.task.service.StatisticService;
import com.korzhov.task.service.StatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api")
public class StatsController {
    private final StatisticService statisticsService;

    @Autowired
    public StatsController(StatisticServiceImpl statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping(value = "/transactions")
    public ResponseEntity<Transaction> create(@RequestBody @Valid Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statisticsService.createTransaction(transaction));
    }


    @GetMapping(value = "/stats")
    public ResponseEntity<StatisticResponse> getStats() {
        return ResponseEntity.ok(statisticsService.getStatistic());
    }

    @DeleteMapping(value = "/transactions")
    public ResponseEntity<Void> delete() {
        statisticsService.deleteStatistic();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
