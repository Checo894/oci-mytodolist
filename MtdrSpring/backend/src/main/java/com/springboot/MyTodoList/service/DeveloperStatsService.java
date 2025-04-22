package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.DeveloperStats;
import com.springboot.MyTodoList.repository.DeveloperStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperStatsService {

    @Autowired
    private DeveloperStatsRepository repository;

    public List<DeveloperStats> getAll() {
        return repository.findAll();
    }

    public Optional<DeveloperStats> getById(Long developerId) {
        return repository.findById(developerId);
    }
}
