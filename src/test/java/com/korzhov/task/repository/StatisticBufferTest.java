package com.korzhov.task.repository;

import com.korzhov.task.model.StatisticEntry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StatisticBufferTest {

    StatisticBuffer statisticBuffer = new StatisticBuffer();

    @Test
    public void should_has_no_nulls_in_buffer() {
        statisticBuffer.clearOrPrepareInMemoryDatabase();
        assertNotNull(statisticBuffer.getEntryByIndex(999), "Entry is not null");
        assertNotNull(statisticBuffer.getEntryByIndex(501), "Entry is not null");
        assertNotNull(statisticBuffer.getEntryByIndex(17), "Entry is not null");
    }

    @Test
    public void should_clear_buffer() {
        StatisticEntry[] data = statisticBuffer.getData();
        data[100] = new StatisticEntry(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, 15, LocalDateTime.now());
        statisticBuffer.setEmpty(false);
        statisticBuffer.clearOrPrepareInMemoryDatabase();
        assertTrue(statisticBuffer.isEmpty(), "Buffer is cleared");
    }

    @Test
    public void should_update_index_counter_to_zero() {
        StatisticBuffer.INDEX_COUNTER = 999;
        statisticBuffer.updateIndexCounter();
        assertEquals(0, StatisticBuffer.INDEX_COUNTER);
    }

}