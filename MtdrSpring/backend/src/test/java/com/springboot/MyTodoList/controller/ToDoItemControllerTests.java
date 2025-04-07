package com.springboot.MyTodoList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ToDoItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoItemService toDoItemService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ Soporte para fechas Java 8+
    }

    @Test
    public void testAddToDoItemWithSprint() throws Exception {
        // Crear Sprint
        Sprint sprint = new Sprint();
        sprint.setId(2L);
        sprint.setSprintNumber(3);
        sprint.setStartDate(LocalDate.parse("2025-03-15"));
        sprint.setEndDate(LocalDate.parse("2025-03-31"));

        // Crear ToDoItem con Sprint
        ToDoItem item = new ToDoItem();
        item.setId(12L);
        item.setTitle("Tarea con Sprint para tu her");
        item.setDescription("Prueba de relación");
        item.setStatus("Not Started");
        item.setSprint(sprint);
        item.setCreation_ts(OffsetDateTime.now());
        item.setStartDate(OffsetDateTime.now());
        item.setActive(true);

        // Mock de la respuesta del servicio
        Mockito.when(toDoItemService.addToDoItem(Mockito.any(ToDoItem.class))).thenReturn(item);

        // Ejecutar y verificar
        mockMvc.perform(post("/todolist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(header().string("location", "/todolist/12"))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("Tarea con Sprint para tu her"))
                .andExpect(jsonPath("$.sprint.sprintNumber").value(3));
                // .andExpect(jsonPath("$.isActive").value(true));
    }
}
