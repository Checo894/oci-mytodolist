package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.DeveloperSprintStats;
import com.springboot.MyTodoList.service.DeveloperSprintStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/developer-sprint-stats")
public class DeveloperSprintStatsController {

    @Autowired
    private DeveloperSprintStatsService service;

    @GetMapping
    public List<DeveloperSprintStats> getAllStats() {
        return service.getAll();
    }

    @GetMapping("/developer/{developerId}")
    public ResponseEntity<?> getStatsByDeveloperId(@PathVariable Long developerId) {
        List<DeveloperSprintStats> stats = service.getByDevId(developerId);

        if (stats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/sprint/{sprintId}")
    public List<DeveloperSprintStats> getStatsBySprintId(@PathVariable Long sprintId) {
        return service.getBySprintId(sprintId);
    }

    @GetMapping("/developer/{developerId}/sprint/{sprintId}")
    public List<DeveloperSprintStats> getStatsByDevAndSprintId(
            @PathVariable Long developerId,
            @PathVariable Long sprintId) {
        return service.getByDevAndSprintIds(developerId, sprintId);
    }
}
