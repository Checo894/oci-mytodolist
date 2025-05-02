/*
package com.springboot.MyTodoList.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeveloperControllerTest {

    @Test
    public void sampleTest() {
        assertTrue(true); // Esto siempre va a pasar
    }
}
*/

package com.springboot.MyTodoList.controller;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;

@SpringBootTest
@AutoConfigureMockMvc
public class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperService developerService;

    // [GET] TEST Create endpoint to retrieve all developers --------------------------------------------------------------------------
    @Test
    public void testGetAllDevelopers() throws Exception {
        // Arrange - Simular respuesta del servicio
        List<Developer> mockDevelopers = List.of(
            new Developer(1L, "John Doe", "+1234567890", "john@example.com",
                          "hashedpassword123", "developer")
        );
        Mockito.when(developerService.getAll()).thenReturn(mockDevelopers);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/developers").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("John Doe"))
            .andExpect(jsonPath("$[0].role").value("developer"));
    }

    // TEST [POST] Create endpoint to register a developer ----------------------------------------------------------------------------
    @Test
    public void shouldRegisterDeveloperSuccessfully() throws Exception {
        mockMvc.perform(post("/developers/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{ \"name\": \"Test Register New User\", \"email\": \"newregister@domain.com\", \"passwordHash\": \"qwer1234\", \"phoneNumber\": \"0000000000\", \"role\": \"projectmanager\" }"))
        .andExpect(status().isOk());

    }

    // TEST [POST] Create endpoint to login a developer -------------------------------------------------------------------------------
    @Test
    public void testLoginSuccess() throws Exception {
        // Arrange
        String email = "test@domain.com";
        String password = "hash123";

        Developer mockDeveloper = new Developer(1L, "Test User", "0000000000", email, password, "developer");

        Mockito.when(developerService.authenticate(email, password)).thenReturn(true);
        Mockito.when(developerService.getByEmail(email)).thenReturn(mockDeveloper);

        String requestBody = "{"
            + "\"email\": \"test@domain.com\","
            + "\"passwordHash\": \"hash123\""
            + "}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/developers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login correcto"))
            .andExpect(jsonPath("$.developerId").value(1))
            .andExpect(jsonPath("$.role").value("developer"))
            .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Arrange
        String email = "wrong@domain.com";
        String password = "wronghash";

        Mockito.when(developerService.authenticate(email, password)).thenReturn(false);

        String requestBody = "{"
            + "\"email\": \"wrong@domain.com\","
            + "\"passwordHash\": \"wronghash\""
            + "}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/developers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        // Arrange
        String email = "notfound@domain.com";
        String password = "validhash";

        Mockito.when(developerService.authenticate(email, password)).thenReturn(true);
        Mockito.when(developerService.getByEmail(email)).thenReturn(null);  // Simula que no lo encuentra

        String requestBody = "{"
            + "\"email\": \"notfound@domain.com\","
            + "\"passwordHash\": \"validhash\""
            + "}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/developers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Usuario no encontrado"));
    }


    // TEST [GET] Create endpoint to get Developer by ID ---------------------------------------------------------------------------
    @Test
    public void testGetDeveloperById_Success() throws Exception {
        // Arrange
        Long developerId = 2L;
        Developer mockDeveloper = new Developer(developerId, "Test User", "0000000000", "test@domain.com", "hash123", "developer");

        Mockito.when(developerService.getById(developerId)).thenReturn(Optional.of(mockDeveloper));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/developers/{id}", developerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@domain.com"));
    }

    @Test
    public void testGetDeveloperById_NotFound() throws Exception {
        // Arrange
        Long developerId = 99L;

        Mockito.when(developerService.getById(developerId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/developers/{id}", developerId))
            .andExpect(status().isNotFound());
    }


    // TEST [PUT] Create endpoint to update Developer ------------------------------------------------------------------------------
    @Test
    public void testUpdateDeveloper_Success() throws Exception {
        // Arrange
        Long developerId = 1L;
        Developer updatedDev = new Developer(developerId, "Updated Name", "1234567890", "updated@domain.com", "updatedHash", "developer");

        Mockito.when(developerService.updateDeveloper(eq(developerId), any(Developer.class))).thenReturn(updatedDev);

        String requestBody = "{"
                + "\"name\": \"Updated Name\","
                + "\"email\": \"updated@domain.com\","
                + "\"passwordHash\": \"updatedHash\","
                + "\"phoneNumber\": \"1234567890\","
                + "\"role\": \"developer\""
                + "}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/developers/{id}", developerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(developerId))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.email").value("updated@domain.com"));
    }

    @Test
    public void testUpdateDeveloper_NotFound() throws Exception {
        // Arrange
        Long developerId = 99L;

        Mockito.when(developerService.updateDeveloper(eq(developerId), any(Developer.class))).thenReturn(null);

        String requestBody = "{"
                + "\"name\": \"Someone\","
                + "\"email\": \"someone@domain.com\","
                + "\"passwordHash\": \"hashpass\","
                + "\"phoneNumber\": \"0000000000\","
                + "\"role\": \"developer\""
                + "}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/developers/{id}", developerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Developer no encontrado"));
    }

    // TEST [DELETE] Create endpoint to delete Developer ---------------------------------------------------------------------------
    @Test
    public void testDeleteDeveloper_Success() throws Exception {
        // Arrange
        Long developerId = 1L;

        Mockito.when(developerService.deleteDeveloper(developerId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/developers/{id}", developerId))
            .andExpect(status().isOk())
            .andExpect(content().string("Developer eliminado correctamente"));
    }
    
    @Test
    public void testDeleteDeveloper_NotFound() throws Exception {
        // Arrange
        Long developerId = 99L;

        Mockito.when(developerService.deleteDeveloper(developerId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/developers/{id}", developerId))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Developer no encontrado"));
    }

}