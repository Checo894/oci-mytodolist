package com.springboot.MyTodoList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Subtask;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class SubtaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubtaskService subtaskService;

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

    // ¡Aquí está la magia que resuelve el problema!
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

}


