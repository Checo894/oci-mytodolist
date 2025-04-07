package com.springboot.MyTodoList.controller;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;

@SpringBootTest
@AutoConfigureMockMvc
public class DeveloperControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperService developerService;

    @Test
    public void testGetAllDevelopers() throws Exception {
        // Arrange - Simular respuesta del servicio
        List<Developer> mockDevelopers = List.of(
            new Developer(1L, "Emiliano Neaves Ortiz", "+526142864991", "a01568982@tec.mx",
                          "66590f8e3ca12526698c2f9b9daf585380f5b92c1320962fd134ba8270ab410e", "developer")
        );
        Mockito.when(developerService.getAll()).thenReturn(mockDevelopers);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/developers").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Emiliano Neaves Ortiz"))
            .andExpect(jsonPath("$[0].role").value("developer"));
    }

    @Test
    public void shouldRegisterDeveloperSuccessfully() throws Exception {
        mockMvc.perform(post("/developers/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{ \"name\": \"Monica Escobedo Cota\", \"email\": \"a01642647@tec.mx\", \"passwordHash\": \"qwer1234\", \"phoneNumber\": \"+523334614250\", \"role\": \"developer\" }"))
        .andExpect(status().isOk());
        
    }
}
