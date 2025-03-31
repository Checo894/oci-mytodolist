package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sprints")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @GetMapping
    public List<Sprint> getAllSprints() {
        return sprintService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Long id) {
        return sprintService.findById(id)
                .map(sprint -> ResponseEntity.ok().body(sprint))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{sprintNumber}")
    public ResponseEntity<Sprint> getSprintByNumber(@PathVariable int sprintNumber) {
        Sprint sprint = sprintService.findBySprintNumber(sprintNumber);
        return sprint != null ? ResponseEntity.ok(sprint) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint) {
        return ResponseEntity.ok(sprintService.createSprint(sprint));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSprint(@PathVariable Long id, @RequestBody Sprint sprint) {
        Sprint updated = sprintService.updateSprint(id, sprint);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long id) {
        boolean deleted = sprintService.deleteSprint(id);
        return deleted ? ResponseEntity.ok("Sprint eliminado correctamente")
                       : ResponseEntity.notFound().build();
    }
}
