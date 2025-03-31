package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    public Optional<Sprint> findById(Long id) {
        return sprintRepository.findById(id);
    }

    public Sprint findBySprintNumber(int sprintNumber) {
        return sprintRepository.findBySprintNumber(sprintNumber);
    }

    public Sprint createSprint(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    public Sprint updateSprint(Long id, Sprint updatedSprint) {
        Optional<Sprint> existing = sprintRepository.findById(id);
        if (existing.isPresent()) {
            Sprint sprint = existing.get();
            sprint.setSprintNumber(updatedSprint.getSprintNumber());
            sprint.setStartDate(updatedSprint.getStartDate());
            sprint.setEndDate(updatedSprint.getEndDate());
            return sprintRepository.save(sprint);
        }
        return null;
    }

    public boolean deleteSprint(Long id) {
        Optional<Sprint> existing = sprintRepository.findById(id);
        if (existing.isPresent()) {
            sprintRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
