package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SprintStats;
import com.springboot.MyTodoList.repository.SprintStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SprintStatsService {

    @Autowired
    private SprintStatsRepository repository;

    public List<SprintStats> getAll() {
        return repository.findAll();
    }

    public Optional<SprintStats> getById(Long sprintId) {
        return repository.findById(sprintId);
    }
}
