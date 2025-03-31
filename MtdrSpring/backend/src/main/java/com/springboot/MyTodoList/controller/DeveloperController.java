package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    @Autowired
    private DeveloperService developerService;

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
            String role = developerService.getRoleByEmail(loginRequest.getEmail());
            return ResponseEntity.ok("Login correcto. Rol: " + role);
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
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
