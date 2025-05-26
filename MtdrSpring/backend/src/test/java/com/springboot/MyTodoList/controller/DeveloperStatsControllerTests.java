package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.DeveloperStats;
import com.springboot.MyTodoList.service.DeveloperStatsService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeveloperStatsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperStatsService service;

    private DeveloperStats createMockStat(Long developerId) {
        DeveloperStats stats = new DeveloperStats();
        stats.setDeveloperId(developerId);
        stats.setTotalAssignedCount(10);
        stats.setTotalCompletedCount(7);
        stats.setSumEstimatedHours(20.0);
        stats.setSumActualHours(18.5);
        stats.setLastUpdatedTs(OffsetDateTime.now());
        return stats;
    }

    @Test
    public void testGetAllStats() throws Exception {
        List<DeveloperStats> mockList = List.of(
                createMockStat(1L),
                createMockStat(2L)
        );

        Mockito.when(service.getAll()).thenReturn(mockList);

        mockMvc.perform(get("/developer-stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].developerId").value(1))
                .andExpect(jsonPath("$[1].developerId").value(2));
    }

    @Test
    public void testGetStatsByDeveloperId_Success() throws Exception {
        DeveloperStats mockStats = createMockStat(5L);

        Mockito.when(service.getById(5L)).thenReturn(Optional.of(mockStats));

        mockMvc.perform(get("/developer-stats/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.developerId").value(5))
                .andExpect(jsonPath("$.totalAssignedCount").value(10))
                .andExpect(jsonPath("$.totalCompletedCount").value(7));
    }

    @Test
    public void testGetStatsByDeveloperId_NotFound() throws Exception {
        Mockito.when(service.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/developer-stats/999"))
                .andExpect(status().isNotFound());
    }
}
