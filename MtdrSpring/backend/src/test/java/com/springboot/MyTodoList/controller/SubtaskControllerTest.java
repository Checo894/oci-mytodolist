package com.springboot.MyTodoList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import com.springboot.MyTodoList.service.SubtaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SubtaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubtaskService subtaskService;

    @MockBean
    private SubtaskRepository subtaskRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddSubtaskToTask() throws Exception {
        Long taskId = 9L;

        Subtask mockSubtask = new Subtask();
        mockSubtask.setId(123L);
        mockSubtask.setTitle("Conectar notificaciones al bot de Telegram");
        mockSubtask.setCompleted(false);
        mockSubtask.setAssignedDeveloperId(44L);
        mockSubtask.setEstimatedHours(2.0);

        ResponseEntity<?> mockResponse = ResponseEntity.status(201).body(mockSubtask);

        Mockito.when(subtaskService.addSubtask(Mockito.eq(taskId), Mockito.any(Subtask.class)))
               .thenAnswer(invocation -> mockResponse);

        mockMvc.perform(post("/subtasks/task/{mainTaskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockSubtask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.title", is("Conectar notificaciones al bot de Telegram")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.assignedDeveloperId", is(44)))
                .andExpect(jsonPath("$.estimatedHours", is(2.0)));
    }

    @Test
    public void testGetAllActiveSubtasks() throws Exception {
        Subtask s = new Subtask();
        s.setId(1L); s.setTitle("Subtarea activa"); s.setActive(true);

        Mockito.when(subtaskService.findAll()).thenReturn(List.of(s));

        mockMvc.perform(get("/subtasks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Subtarea activa")));
    }

    @Test
    public void testGetSubtaskById() throws Exception {
        Subtask s = new Subtask();
        s.setId(1L); s.setTitle("Subtarea por ID"); s.setActive(true);

        Mockito.when(subtaskService.getSubtaskById(1L)).thenReturn(ResponseEntity.of(Optional.of(s)));

        mockMvc.perform(get("/subtasks/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Subtarea por ID")));
    }

   @Test
    public void testGetSubtaskDetails() throws Exception {
        // Crear la subtarea simulada
        Subtask s = new Subtask();
        s.setId(1L);
        s.setTitle("Subtarea con detalles");
        s.setEstimatedHours(2.0);
        s.setActive(true);

        // Mock del ToDoItem asociado
        ToDoItem mockMainTask = new ToDoItem();
        mockMainTask.setTitle("Tarea principal");
        mockMainTask.setDescription("Descripción de la tarea principal");

        // Mock del Sprint (opcional)
        Sprint sprint = new Sprint();
        sprint.setId(10L);
        sprint.setSprintNumber(5);
        sprint.setStartDate(java.time.LocalDate.parse("2025-04-01"));
        sprint.setEndDate(java.time.LocalDate.parse("2025-04-10"));


        mockMainTask.setSprint(sprint);
        s.setMainTask(mockMainTask);

        // Simula que el repo devuelve la subtarea
        Mockito.when(subtaskRepository.findById(1L)).thenReturn(Optional.of(s));

        // Ejecutar el GET y validar la respuesta
        mockMvc.perform(get("/subtasks/1/details").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Subtarea con detalles")))
                .andExpect(jsonPath("$.estimatedHours", is(2.0)))
                .andExpect(jsonPath("$.task.title", is("Tarea principal")))
                .andExpect(jsonPath("$.task.description", is("Descripción de la tarea principal")))
                .andExpect(jsonPath("$.task.sprint.sprintNumber", is(5)))
                .andExpect(jsonPath("$.task.sprint.startDate", is("2025-04-01")))
                .andExpect(jsonPath("$.task.sprint.endDate", is("2025-04-10")));
    }

    @Test
    public void testGetSubtasksByParentTask() throws Exception {
        Long taskId = 9L;
        Subtask s1 = new Subtask(); s1.setId(1L); s1.setTitle("Sub de tarea 9");

        Mockito.when(subtaskService.findByMainTaskId(taskId)).thenReturn(List.of(s1));

        mockMvc.perform(get("/subtasks/task/{mainTaskId}", taskId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Sub de tarea 9")));
    }

    @Test
    public void testGetAllSubtasksIncludingInactive() throws Exception {
        Subtask active = new Subtask(); active.setId(1L); active.setTitle("Activa");
        Subtask inactive = new Subtask(); inactive.setId(2L); inactive.setTitle("Inactiva");

        Mockito.when(subtaskService.getAllSubtasksIncludingInactive()).thenReturn(List.of(active, inactive));

        mockMvc.perform(get("/subtasks/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testUpdateSubtask() throws Exception {
        Long subtaskId = 1L;

        Subtask updatedSubtask = new Subtask();
        updatedSubtask.setId(subtaskId);
        updatedSubtask.setTitle("Subtarea actualizada");
        updatedSubtask.setCompleted(true);
        updatedSubtask.setAssignedDeveloperId(44L);
        updatedSubtask.setEstimatedHours(2.5);
        updatedSubtask.setActualHours(2.0);

        Mockito.when(subtaskService.updateSubtask(Mockito.eq(subtaskId), Mockito.any(Subtask.class)))
               .thenReturn(ResponseEntity.ok(updatedSubtask));

        mockMvc.perform(put("/subtasks/{id}", subtaskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSubtask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Subtarea actualizada")))
                .andExpect(jsonPath("$.completed", is(true)))
                .andExpect(jsonPath("$.assignedDeveloperId", is(44)))
                .andExpect(jsonPath("$.estimatedHours", is(2.5)))
                .andExpect(jsonPath("$.actualHours", is(2.0)));
    }

    @Test
    public void testDeleteSubtask() throws Exception {
        Long subtaskId = 1L;

        Mockito.when(subtaskService.deleteSubtask(subtaskId))
               .thenReturn(ResponseEntity.ok("Subtarea desactivada correctamente"));

        mockMvc.perform(delete("/subtasks/{id}", subtaskId))
                .andExpect(status().isOk())
                .andExpect(content().string("Subtarea desactivada correctamente"));
    }
}