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

    // 🔄 ahora solo devuelve subtareas activas
    public List<Subtask> findAll() {
        return subtaskRepository.findByIsActiveTrue();
    }

    // 🔄 ahora solo devuelve subtareas activas por tarea
    public List<Subtask> findByMainTaskId(Long mainTaskId) {
        return subtaskRepository.findByMainTaskIdAndIsActiveTrue(mainTaskId);
    }

    // 🔄 se valida que esté activa
    public ResponseEntity<Subtask> getSubtaskById(Long id) {
        Optional<Subtask> subtask = subtaskRepository.findById(id);
        if (subtask.isPresent() && subtask.get().isActive()) {
            return new ResponseEntity<>(subtask.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> addSubtask(Long mainTaskId, Subtask subtask) {
        try {
            logger.info("Intentando agregar subtarea para mainTaskId: {}", mainTaskId);

            Optional<ToDoItem> mainTask = toDoItemRepository.findById(mainTaskId);
            if (!mainTask.isPresent()) {
                logger.error("Tarea principal con ID {} no encontrada", mainTaskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarea principal no encontrada");
            }

            if (subtask.getEstimatedHours() == null || subtask.getEstimatedHours() < 0 || subtask.getEstimatedHours() > 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⚠️ Las horas estimadas deben estar entre 0.0 y 4.0");
            }            

            subtask.setMainTask(mainTask.get());
            subtask.setActive(true); // 🔄 aseguramos que se cree como activa
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

    // 🔄 cambio a borrado lógico
    public ResponseEntity<String> deleteSubtask(Long id) {
        Optional<Subtask> optionalSubtask = subtaskRepository.findById(id);
        if (optionalSubtask.isPresent() && optionalSubtask.get().isActive()) {
            Subtask subtask = optionalSubtask.get();
            subtask.setActive(false);
            subtaskRepository.save(subtask);
            return new ResponseEntity<>("Subtarea desactivada correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Subtarea no encontrada", HttpStatus.NOT_FOUND);
    }

    public List<Subtask> getAllSubtasksIncludingInactive() {
        return subtaskRepository.findAll(); // sin filtro
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
