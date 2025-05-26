package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.SprintStats;
import com.springboot.MyTodoList.service.SprintStatsService;

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
public class SprintStatsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintStatsService service;

    private SprintStats createMockSprintStat(Long sprintId) {
        SprintStats stats = new SprintStats();
        stats.setSprintId(sprintId);
        stats.setTotalSubtasks(12);
        stats.setTotalCompleted(10);
        stats.setSumEstimatedHours(25.5);
        stats.setSumActualHours(24.0);
        stats.setLastUpdatedTs(OffsetDateTime.now());
        return stats;
    }

    @Test
    public void testGetAllSprintStats() throws Exception {
        List<SprintStats> mockStats = List.of(
                createMockSprintStat(1L),
                createMockSprintStat(2L)
        );

        Mockito.when(service.getAll()).thenReturn(mockStats);

        mockMvc.perform(get("/sprint-stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sprintId").value(1))
                .andExpect(jsonPath("$[1].sprintId").value(2));
    }

    @Test
    public void testGetSprintStatsById_Success() throws Exception {
        SprintStats mockStat = createMockSprintStat(5L);

        Mockito.when(service.getById(5L)).thenReturn(Optional.of(mockStat));

        mockMvc.perform(get("/sprint-stats/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintId").value(5))
                .andExpect(jsonPath("$.totalSubtasks").value(12))
                .andExpect(jsonPath("$.totalCompleted").value(10));
    }

    @Test
    public void testGetSprintStatsById_NotFound() throws Exception {
        Mockito.when(service.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/sprint-stats/999"))
                .andExpect(status().isNotFound());
    }
}
