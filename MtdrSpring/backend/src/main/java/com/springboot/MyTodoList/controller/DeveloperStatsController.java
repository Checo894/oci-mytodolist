package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.DeveloperStats;
import com.springboot.MyTodoList.service.DeveloperStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing DeveloperStats endpoints.
 */

@RestController
@RequestMapping("/developer-stats")
public class DeveloperStatsController {

    private final DeveloperStatsService developerStatsService;

    public DeveloperStatsController(DeveloperStatsService developerStatsService) {
        this.developerStatsService = developerStatsService;
    }

    /**
     * Retrieves all developer statistics.
     *
     * @return List of DeveloperStats
     */

    @GetMapping
    public List<DeveloperStats> getAllStats() {
        return developerStatsService.getAll();
    }

    /**
     * Retrieves statistics for a specific developer.
     *
     * @param developerId the ID of the developer
     * @return DeveloperStats if found, 404 otherwise
     */
    
    @GetMapping("/{developerId}")
    public ResponseEntity<DeveloperStats> getStatsByDeveloperId(@PathVariable Long developerId) {
        return developerStatsService.getById(developerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
