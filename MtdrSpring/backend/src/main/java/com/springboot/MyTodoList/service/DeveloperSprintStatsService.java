package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.DeveloperSprintStats;
import com.springboot.MyTodoList.repository.DeveloperSprintStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeveloperSprintStatsService {

    @Autowired
    private DeveloperSprintStatsRepository repository;

    public List<DeveloperSprintStats> getAll() {
        return repository.findAll();
    }

    public List<DeveloperSprintStats> getByDevId(Long developerId) {
        return repository.findByDeveloperId(developerId);
    }

    public List<DeveloperSprintStats> getBySprintId(Long sprintId) {
        return repository.findBySprintId(sprintId);
    }

    public List<DeveloperSprintStats> getByDevAndSprintIds(Long developerId, Long sprintId) {
        return repository.findByDeveloperIdAndSprintId(developerId, sprintId);
    }
}
