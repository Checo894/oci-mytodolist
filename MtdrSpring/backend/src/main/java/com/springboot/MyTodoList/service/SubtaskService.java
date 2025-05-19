package com.springboot.MyTodoList.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import com.springboot.MyTodoList.repository.ToDoItemRepository;

@Service
public class SubtaskService {

    private static final Logger logger = LoggerFactory.getLogger(SubtaskService.class);

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private ToDoItemRepository toDoItemRepository;


    public List<Subtask> findAll() {
        return subtaskRepository.findActiveSubtask();
    }

    public List<Subtask> findByMainTaskId(Long mainTaskId) {
        return subtaskRepository.findByMainTaskIdAndIsActiveTrue(mainTaskId);
    }

    public ResponseEntity<Subtask> getSubtaskById(Long id) {
        Optional<Subtask> subtask = subtaskRepository.findById(id);
        if (subtask.isPresent() && subtask.get().isActive()) {
            return new ResponseEntity<>(subtask.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> addSubtask(Long mainTaskId, Subtask subtask) {
        try {
            logger.info("Trying to add subtask for mainTaskId: {}", mainTaskId);

            Optional<ToDoItem> mainTask = toDoItemRepository.findById(mainTaskId);
            if (!mainTask.isPresent()) {
                logger.error("Main task with ID {} ​​not found", mainTaskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Main task not found");
            }

            if (subtask.getEstimatedHours() == null || subtask.getEstimatedHours() < 0 || subtask.getEstimatedHours() > 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⚠️ Estimated hours should be between 0.0 and 4.0");
            }            

            subtask.setMainTask(mainTask.get());
            subtask.setActive(true); 
            logger.info("Subtask before saving: {}", subtask);
            Subtask savedSubtask = subtaskRepository.save(subtask);
            logger.info("Subtask saved successfully: {}", savedSubtask);
            return new ResponseEntity<>(savedSubtask, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating subtask: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating subtask: " + e.getMessage());
        }
    }

    public ResponseEntity<Subtask> updateSubtask(Long id, Subtask updatedSubtask) {
        Optional<Subtask> existingSubtask = subtaskRepository.findById(id);
        if (existingSubtask.isPresent() && existingSubtask.get().isActive()) {
            Subtask subtask = existingSubtask.get();
            subtask.setTitle(updatedSubtask.getTitle());
            subtask.setCompleted(updatedSubtask.isCompleted());
            subtask.setAssignedDeveloperId(updatedSubtask.getAssignedDeveloperId());
            subtask.setEstimatedHours(updatedSubtask.getEstimatedHours());
            if (updatedSubtask.isCompleted()) {
                if (updatedSubtask.getActualHours() == null || updatedSubtask.getActualHours() <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                subtask.setActualHours(updatedSubtask.getActualHours());
            }
            return new ResponseEntity<>(subtaskRepository.save(subtask), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    
    public ResponseEntity<String> deleteSubtask(Long id) {
        Optional<Subtask> optionalSubtask = subtaskRepository.findById(id);
        if (optionalSubtask.isPresent() && optionalSubtask.get().isActive()) {
            Subtask subtask = optionalSubtask.get();
            subtask.setActive(false);
            subtaskRepository.save(subtask);
            return new ResponseEntity<>("Subtask desactivated succesfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Subtask not found", HttpStatus.NOT_FOUND);
    }

    public List<Subtask> getAllSubtasksIncludingInactive() {
        return subtaskRepository.findAll(); 
    }

    public List<Subtask> getSubtasksByDeveloper(Long developerId) {
        return subtaskRepository.findByAssignedDeveloperIdAndIsActiveTrue(developerId);
    }

    public boolean existsByTitle(String title) {
        return subtaskRepository.findByTitle(title).size() > 0;
    }
    
    public Subtask getByTitleAndDeveloper(String title, Long developerId) {
        return subtaskRepository.findByTitleAndAssignedDeveloperId(title, developerId)
                                .stream()
                                .findFirst()
                                .orElse(null);
    }
    
    
}
