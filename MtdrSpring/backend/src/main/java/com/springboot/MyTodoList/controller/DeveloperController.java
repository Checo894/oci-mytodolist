package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    @Autowired
    private DeveloperService developerService;

    @GetMapping
    public ResponseEntity<?> getAllDevelopers() {
        return ResponseEntity.ok(developerService.getAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Developer developer) {
        Developer created = developerService.createDeveloper(developer);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Developer loginRequest) {
        boolean valid = developerService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPasswordHash()
        );

        if (valid) {
            Developer dev = developerService.getByEmail(loginRequest.getEmail());
            if (dev == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login correcto");
            response.put("role", dev.getRole());
            response.put("developerId", dev.getId());
            response.put("name", dev.getName());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales incorrectas"));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getDeveloperById(@PathVariable Long id) {
        return developerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Developer>build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDeveloper(@PathVariable Long id, @RequestBody Developer updatedDev) {
        Developer updated = developerService.updateDeveloper(id, updatedDev);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer no encontrado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeveloper(@PathVariable Long id) {
        boolean deleted = developerService.deleteDeveloper(id);
        if (deleted) {
            return ResponseEntity.ok("Developer eliminado correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer no encontrado");
    }

}
