package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.*;
import com.springboot.MyTodoList.service.DeveloperSprintStatsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeveloperSprintStatsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperSprintStatsService service;

    private DeveloperSprintStats createMockStat(Long devId, Long sprintId) {
        Developer dev = new Developer(devId, "Dev " + devId, "0000000000", "dev" + devId + "@mail.com", "hash123", "developer");
        Sprint sprint = new Sprint(sprintId, (int)(long)sprintId, OffsetDateTime.now().minusDays(5).toLocalDate(), OffsetDateTime.now().toLocalDate());
        DeveloperSprintStatsId id = new DeveloperSprintStatsId();
        id.setDeveloperId(devId);
        id.setSprintId(sprintId);

        return new DeveloperSprintStats(
            id, dev, sprint,
            10, 8,
            20.5, 19.0
        );
    }

    @Test
    public void testGetAllStats() throws Exception {
        List<DeveloperSprintStats> stats = List.of(
                createMockStat(1L, 1L),
                createMockStat(2L, 1L)
        );
        Mockito.when(service.getAll()).thenReturn(stats);

        mockMvc.perform(get("/developer-sprint-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetStatsByDeveloperId_Success() throws Exception {
        List<DeveloperSprintStats> stats = List.of(createMockStat(1L, 1L));
        Mockito.when(service.getByDevId(1L)).thenReturn(stats);

        mockMvc.perform(get("/developer-sprint-stats/developer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].developer.name").value("Dev 1"));
    }

    @Test
    public void testGetStatsByDeveloperId_NotFound() throws Exception {
        Mockito.when(service.getByDevId(999L)).thenReturn(List.of());

        mockMvc.perform(get("/developer-sprint-stats/developer/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStatsBySprintId() throws Exception {
        List<DeveloperSprintStats> stats = List.of(createMockStat(1L, 2L));
        Mockito.when(service.getBySprintId(2L)).thenReturn(stats);

        mockMvc.perform(get("/developer-sprint-stats/sprint/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sprint.id").value(2));
    }

    @Test
    public void testGetStatsByDevAndSprintId() throws Exception {
        List<DeveloperSprintStats> stats = List.of(createMockStat(3L, 3L));
        Mockito.when(service.getByDevAndSprintIds(3L, 3L)).thenReturn(stats);

        mockMvc.perform(get("/developer-sprint-stats/developer/3/sprint/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].developer.id").value(3))
                .andExpect(jsonPath("$[0].sprint.id").value(3));
    }
}
