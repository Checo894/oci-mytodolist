// SprintService.java
package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SprintService {

    private final SprintRepository repository;

    public SprintService(SprintRepository repository) {
        this.repository = repository;
    }

    public List<Sprint> getAll() {
        return repository.findAll();
    }

    public Optional<Sprint> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<Sprint> getByNumber(int number) {
        return Optional.ofNullable(repository.findBySprintNumber(number));
    }

    public Sprint create(Sprint sprint) {
        return repository.save(sprint);
    }

    public Optional<Sprint> update(Long id, Sprint updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setSprintNumber(updated.getSprintNumber());
                    existing.setStartDate(updated.getStartDate());
                    existing.setEndDate(updated.getEndDate());
                    return repository.save(existing);
                });
    }

    public void delete(Long id) {
        repository.findById(id)
                  .ifPresent(repository::delete);
    }
}
