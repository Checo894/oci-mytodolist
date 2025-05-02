package com.springboot.MyTodoList.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 
    }

    @Test
    public void testGetAllSprints() throws Exception {
        Sprint sprint1 = new Sprint(1L, 1, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 15));
        Sprint sprint2 = new Sprint(2L, 2, LocalDate.of(2025, 3, 16), LocalDate.of(2025, 3, 31));

        List<Sprint> sprints = List.of(sprint1, sprint2);

        Mockito.when(sprintService.findAll()).thenReturn(sprints);

        mockMvc.perform(get("/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sprintNumber").value(1))
                .andExpect(jsonPath("$[1].sprintNumber").value(2));
    }
    @Test
    public void testGetSprintById() throws Exception {
        Sprint sprint = new Sprint(3L, 3, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 15));

        Mockito.when(sprintService.findById(3L)).thenReturn(java.util.Optional.of(sprint));

        mockMvc.perform(get("/sprints/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.sprintNumber").value(3));
    }

    @Test
    public void testGetSprintById_NotFound() throws Exception {
        Mockito.when(sprintService.findById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/sprints/999"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetSprintByNumber() throws Exception {
        Sprint sprint = new Sprint(4L, 4, LocalDate.of(2025, 4, 16), LocalDate.of(2025, 4, 30));

        Mockito.when(sprintService.findBySprintNumber(4)).thenReturn(sprint);

        mockMvc.perform(get("/sprints/number/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.sprintNumber").value(4));
    }

    @Test
    public void testGetSprintByNumber_NotFound() throws Exception {
        Mockito.when(sprintService.findBySprintNumber(999)).thenReturn(null);

        mockMvc.perform(get("/sprints/number/999"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testCreateSprint() throws Exception {
        Sprint sprintToCreate = new Sprint(null, 5, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 15));
        Sprint savedSprint = new Sprint(5L, 5, sprintToCreate.getStartDate(), sprintToCreate.getEndDate());

        Mockito.when(sprintService.createSprint(Mockito.any(Sprint.class))).thenReturn(savedSprint);

        mockMvc.perform(post("/sprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprintToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.sprintNumber").value(5));
    }
    @Test
    public void testUpdateSprint() throws Exception {
        Sprint updatedSprint = new Sprint(6L, 6, LocalDate.of(2025, 5, 16), LocalDate.of(2025, 5, 31));

        Mockito.when(sprintService.updateSprint(Mockito.eq(6L), Mockito.any(Sprint.class)))
                .thenReturn(updatedSprint);

        mockMvc.perform(put("/sprints/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSprint)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.sprintNumber").value(6));
    }

    @Test
    public void testUpdateSprint_NotFound() throws Exception {
        Sprint updatedSprint = new Sprint(999L, 99, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 15));

        Mockito.when(sprintService.updateSprint(Mockito.eq(999L), Mockito.any(Sprint.class)))
                .thenReturn(null);

        mockMvc.perform(put("/sprints/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSprint)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDeleteSprint() throws Exception {
        Mockito.when(sprintService.deleteSprint(8L)).thenReturn(true);

        mockMvc.perform(delete("/sprints/8"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sprint eliminado correctamente"));
    }

    @Test
    public void testDeleteSprint_NotFound() throws Exception {
        Mockito.when(sprintService.deleteSprint(999L)).thenReturn(false);

        mockMvc.perform(delete("/sprints/999"))
                .andExpect(status().isNotFound());
    }
}