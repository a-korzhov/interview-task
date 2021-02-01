package com.korzhov.task.web;

import com.korzhov.task.domain.StatisticEntry;
import com.korzhov.task.service.StatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mvc;

    private final static String API_TRANSACTIONS = "/api/transactions";
    private final static String API_STATISTICS = "/api/stats";
    @MockBean
    StatisticService statisticService;

    @Test
    public void should_add_transaction() throws Exception {
        mvc.perform(post(API_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction(new BigDecimal("10.03"), LocalDateTime.now(ZoneOffset.UTC))))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_not_accept_old_transaction() throws Exception {
        mvc.perform(post(API_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction(new BigDecimal("12.20"), LocalDateTime.now(ZoneOffset.UTC).minusMinutes(6))))
                .andExpect(status().isNoContent());
    }


    @Test
    public void should_return_bad_request_if_transaction_in_future() throws Exception {
        mvc.perform(post(API_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
                .content(createTransaction(new BigDecimal("1.23"), LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_statistics() throws Exception {
        when(statisticService.getStats()).thenReturn(
                new StatisticEntry(BigDecimal.valueOf(4.00), BigDecimal.valueOf(2.00), BigDecimal.valueOf(3.00), BigDecimal.valueOf(1.00), 2,
                        LocalDateTime.now())
        );

        mvc.perform(get(API_STATISTICS).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json")).andExpect(jsonPath("count", is(2)))
                .andExpect(jsonPath("sum", is("4.00"))).andExpect(jsonPath("avg", is("2.00")));
    }

    @Test
    public void should_delete_all_transaction() throws Exception {
        mvc.perform(delete(API_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private String createTransaction(BigDecimal amount, LocalDateTime timestamp) {
        return "{\"amount\":\"" + amount.toString() + "\",\"timestamp\":\"" + timestamp.toString() + "\"}";
    }
}