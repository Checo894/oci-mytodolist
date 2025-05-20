package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.DeveloperStats;
import com.springboot.MyTodoList.repository.DeveloperStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling business logic related to DeveloperStats.
 */

@Service
public class DeveloperStatsService {

    private final DeveloperStatsRepository developerStatsRepository;

    public DeveloperStatsService(DeveloperStatsRepository developerStatsRepository) {
        this.developerStatsRepository = developerStatsRepository;
    }

    /**
     * Retrieves all developer statistics.
     *
     * @return List of DeveloperStats
     */

    public List<DeveloperStats> getAll() {
        return developerStatsRepository.findAll();
    }

    /**
     * Retrieves statistics for a specific developer.
     *
     * @param developerId the ID of the developer
     * @return Optional containing DeveloperStats if found, or empty
     */

    public Optional<DeveloperStats> getById(Long developerId) {
        return developerStatsRepository.findById(developerId);
    }
}

