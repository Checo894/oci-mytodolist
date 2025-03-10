package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskService {

    private static final Logger logger = LoggerFactory.getLogger(SubtaskService.class);

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    public List<Subtask> findAll() {
        return subtaskRepository.findAll();
    }

    public List<Subtask> findByMainTaskId(Long mainTaskId) {
        return subtaskRepository.findByMainTaskId(mainTaskId);
    }

    public ResponseEntity<Subtask> getSubtaskById(Long id) {
        Optional<Subtask> subtask = subtaskRepository.findById(id);
        return subtask.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> addSubtask(Long mainTaskId, Subtask subtask) {
        try {
            logger.info("Intentando agregar subtarea para mainTaskId: {}", mainTaskId);
            
            Optional<ToDoItem> mainTask = toDoItemRepository.findById(mainTaskId);
            if (!mainTask.isPresent()) {
                logger.error("Tarea principal con ID {} no encontrada", mainTaskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarea principal no encontrada");
            }
            
            subtask.setMainTask(mainTask.get());
            logger.info("Subtarea antes de guardarse: {}", subtask);
            Subtask savedSubtask = subtaskRepository.save(subtask);
            logger.info("Subtarea guardada correctamente: {}", savedSubtask);
            return new ResponseEntity<>(savedSubtask, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error al crear la subtarea: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear la subtarea: " + e.getMessage());
        }
    }

    public ResponseEntity<Subtask> updateSubtask(Long id, Subtask updatedSubtask) {
        Optional<Subtask> existingSubtask = subtaskRepository.findById(id);
        if (existingSubtask.isPresent()) {
            Subtask subtask = existingSubtask.get();
            subtask.setTitle(updatedSubtask.getTitle());
            subtask.setCompleted(updatedSubtask.isCompleted());
            subtask.setAssignedDeveloperId(updatedSubtask.getAssignedDeveloperId());
            return new ResponseEntity<>(subtaskRepository.save(subtask), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> deleteSubtask(Long id) {
        if (subtaskRepository.existsById(id)) {
            subtaskRepository.deleteById(id);
            return new ResponseEntity<>("Subtarea eliminada correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Subtarea no encontrada", HttpStatus.NOT_FOUND);
    }
}
