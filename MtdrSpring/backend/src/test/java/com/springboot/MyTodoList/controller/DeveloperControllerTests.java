package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeveloperControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperService developerService;

    @Test
    public void testGetAllDevelopers() throws Exception {
        List<Developer> mockDevelopers = List.of(
            new Developer(1L, "Emiliano Neaves Ortiz", "+526142864991", "a01568982@tec.mx",
                          "66590f8e3ca12526698c2f9b9daf585380f5b92c1320962fd134ba8270ab410e", "developer")
        );
        Mockito.when(developerService.getAll()).thenReturn(mockDevelopers);

        mockMvc.perform(get("/developers").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Emiliano Neaves Ortiz"))
            .andExpect(jsonPath("$[0].role").value("developer"));
    }

    @Test
    public void testGetDeveloperById() throws Exception {
        Long devId = 1L;

        Developer dev = new Developer(devId, "Emiliano Neaves Ortiz", "+526142864991",
                "a01568982@tec.mx",
                "hashedpassword",
                "developer");

        Mockito.when(developerService.getById(devId))
               .thenReturn(Optional.of(dev));

        mockMvc.perform(get("/developers/{id}", devId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Emiliano Neaves Ortiz")))
                .andExpect(jsonPath("$.phoneNumber", is("+526142864991")))
                .andExpect(jsonPath("$.email", is("a01568982@tec.mx")))
                .andExpect(jsonPath("$.role", is("developer")));
    }

    @Test
    public void testGetDeveloperById_NotFound() throws Exception {
        Long devId = 999L;

        Mockito.when(developerService.getById(devId))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/developers/{id}", devId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
