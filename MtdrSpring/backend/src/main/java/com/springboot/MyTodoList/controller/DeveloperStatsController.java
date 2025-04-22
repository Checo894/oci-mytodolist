package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.DeveloperStats;
import com.springboot.MyTodoList.service.DeveloperStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/developer-stats")
public class DeveloperStatsController {

    @Autowired
    private DeveloperStatsService service;

    @GetMapping
    public List<DeveloperStats> getAllStats() {
        return service.getAll();
    }

    @GetMapping("/{developerId}")
    public ResponseEntity<?> getStatsByDeveloperId(@PathVariable Long developerId) {
        return service.getById(developerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
