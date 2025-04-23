package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.SprintStats;
import com.springboot.MyTodoList.service.SprintStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sprint-stats")
public class SprintStatsController {

    @Autowired
    private SprintStatsService service;

    @GetMapping
    public List<SprintStats> getAllStats() {
        return service.getAll();
    }

    @GetMapping("/{sprintId}")
    public ResponseEntity<?> getStatsBySprintId(@PathVariable Long sprintId) {
        return service.getById(sprintId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
