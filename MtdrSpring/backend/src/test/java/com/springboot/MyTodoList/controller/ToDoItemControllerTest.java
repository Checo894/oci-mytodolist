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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                // .andExpect(jsonPath("$.isActive").value(true)); // Línea comentada temporalmente
    }

    @Test
    public void testGetAllToDoItems() throws Exception {
        // Crear lista simulada
        ToDoItem item1 = new ToDoItem();
        item1.setId(1L);
        item1.setTitle("Tarea 1");
        item1.setStatus("Not Started");
        item1.setActive(true);

        ToDoItem item2 = new ToDoItem();
        item2.setId(2L);
        item2.setTitle("Tarea 2");
        item2.setStatus("In Progress");
        item2.setActive(true);

        List<ToDoItem> itemList = List.of(item1, item2);

        // Mock del servicio
        Mockito.when(toDoItemService.findAll()).thenReturn(itemList);

        // Llamada al endpoint y verificación
        mockMvc.perform(get("/todolist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Tarea 1"))
                .andExpect(jsonPath("$[1].status").value("In Progress"));
    }
    @Test
    public void testGetToDoItemById() throws Exception {
        ToDoItem item = new ToDoItem();
        item.setId(5L);
        item.setTitle("Tarea individual");
        item.setStatus("In Progress");
        item.setActive(true);

        Mockito.when(toDoItemService.getItemById(5L)).thenReturn(
                new ResponseEntity<>(item, HttpStatus.OK)
        );

        mockMvc.perform(get("/todolist/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Tarea individual"))
                .andExpect(jsonPath("$.status").value("In Progress"));
    }

    @Test
    public void testGetToDoItemById_NotFound() throws Exception {
        Mockito.when(toDoItemService.getItemById(99L)).thenReturn(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );

        mockMvc.perform(get("/todolist/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllToDoItemsIncludingInactive() throws Exception {
        ToDoItem activeItem = new ToDoItem();
        activeItem.setId(1L);
        activeItem.setTitle("Activa");
        activeItem.setActive(true);

        ToDoItem inactiveItem = new ToDoItem();
        inactiveItem.setId(2L);
        inactiveItem.setTitle("Inactiva");
        inactiveItem.setActive(false);

        List<ToDoItem> allItems = List.of(activeItem, inactiveItem);

        Mockito.when(toDoItemService.getAllItemsIncludingInactive()).thenReturn(allItems);

        mockMvc.perform(get("/todolist/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Activa"))
                .andExpect(jsonPath("$[1].active").value(false));
    }
    @Test
    public void testUpdateToDoItemById() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
    
        ToDoItem original = new ToDoItem();
        original.setId(10L);
        original.setTitle("Tarea original");
        original.setCreation_ts(now);
        original.setStartDate(now);
        original.setStatus("Not Started");
    
        ToDoItem updated = new ToDoItem();
        updated.setId(10L);
        updated.setTitle("Tarea actualizada");
        updated.setCreation_ts(now);
        updated.setStartDate(now);
        updated.setStatus("In Progress");
    
        Mockito.when(toDoItemService.updateToDoItem(Mockito.eq(10L), Mockito.any(ToDoItem.class)))
                .thenReturn(updated);
    
        mockMvc.perform(put("/todolist/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Tarea actualizada"))
                .andExpect(jsonPath("$.status").value("In Progress"));
    }
    
    @Test
    public void testUpdateToDoItemById_NotFound() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
    
        ToDoItem original = new ToDoItem();
        original.setId(999L);
        original.setTitle("Tarea que no existe");
        original.setCreation_ts(now);
        original.setStartDate(now);
        original.setStatus("Not Started");
    
        Mockito.when(toDoItemService.updateToDoItem(Mockito.eq(999L), Mockito.any(ToDoItem.class)))
                .thenReturn(null);
    
        mockMvc.perform(put("/todolist/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(original)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tarea no encontrada"));
    }
    @Test
    public void testDeleteToDoItemById() throws Exception {
        Mockito.when(toDoItemService.deleteToDoItem(7L)).thenReturn(true);

        mockMvc.perform(delete("/todolist/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea eliminada correctamente"));
    }

    @Test
    public void testDeleteToDoItemById_NotFound() throws Exception {
        Mockito.when(toDoItemService.deleteToDoItem(777L)).thenReturn(false);

        mockMvc.perform(delete("/todolist/777"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tarea no encontrada"));
    }
    @Test
    public void testGetToDoItemsByStatus() throws Exception {
        ToDoItem item1 = new ToDoItem();
        item1.setId(1L);
        item1.setTitle("Tarea 1");
        item1.setStatus("Completed");

        ToDoItem item2 = new ToDoItem();
        item2.setId(2L);
        item2.setTitle("Tarea 2");
        item2.setStatus("Completed");

        List<ToDoItem> completedItems = List.of(item1, item2);

        Mockito.when(toDoItemService.findAll()).thenReturn(completedItems);

        mockMvc.perform(get("/todolist/status/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("Completed"));
    }

    @Test
    public void testGetToDoItemsByStatus_NotFound() throws Exception {
        Mockito.when(toDoItemService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/todolist/status/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontraron tareas con el estado: unknown"));
    }
}