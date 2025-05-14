package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.SubtaskDetailDTO;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import com.springboot.MyTodoList.service.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/subtasks")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    @Autowired
    private SubtaskRepository subtaskRepository;


    @GetMapping
    public List<Subtask> getAllSubtasks() {
        return subtaskService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subtask> getSubtaskById(@PathVariable Long id) {
        return subtaskService.getSubtaskById(id);
    }

    @GetMapping("/task/{mainTaskId}")
    public List<Subtask> getSubtasksByMainTask(@PathVariable Long mainTaskId) {
        return subtaskService.findByMainTaskId(mainTaskId);
    }

    @PostMapping("/task/{mainTaskId}")
    public ResponseEntity<?> addSubtask(@PathVariable Long mainTaskId, @RequestBody Subtask subtask) {
        return subtaskService.addSubtask(mainTaskId, subtask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Long id, @RequestBody Subtask subtask) {
        return subtaskService.updateSubtask(id, subtask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubtask(@PathVariable Long id) {
        return subtaskService.deleteSubtask(id);
    }

    // ⚠️ Este método no está filtrado por isActive. Solo para uso administrativo.
    @GetMapping("/all")
    public List<Subtask> getAllRawSubtasks() {
        return subtaskService.getAllSubtasksIncludingInactive();
    }

    @GetMapping("/developer/{developerId}")
    public List<Subtask> getSubtasksByDeveloper(@PathVariable Long developerId) {
        return subtaskService.getSubtasksByDeveloper(developerId);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getSubtaskDetails(@PathVariable Long id) {
        Optional<Subtask> opt = subtaskRepository.findById(id);

        if (opt.isEmpty() || !opt.get().isActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Subtarea no encontrada o desactivada"));
        }

        Subtask s = opt.get();
        ToDoItem task = s.getMainTask();

        SubtaskDetailDTO dto = new SubtaskDetailDTO();
        dto.id = s.getId();
        dto.title = s.getTitle();
        dto.estimatedHours = s.getEstimatedHours();
        dto.actualHours = s.getActualHours();
        dto.completed = s.isCompleted();

        SubtaskDetailDTO.TaskInfo taskInfo = new SubtaskDetailDTO.TaskInfo();
        taskInfo.title = task.getTitle();
        taskInfo.description = task.getDescription();
        taskInfo.status = task.getStatus();
        taskInfo.progress = task.getProgress();

        if (task.getSprint() != null) {
            Sprint sprint = task.getSprint();
            SubtaskDetailDTO.SprintInfo sprintInfo = new SubtaskDetailDTO.SprintInfo();
            sprintInfo.id = sprint.getId();
            sprintInfo.sprintNumber = sprint.getSprintNumber();
            sprintInfo.startDate = sprint.getStartDate();
            sprintInfo.endDate = sprint.getEndDate();
            taskInfo.sprint = sprintInfo;
        }

        dto.task = taskInfo;

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getAllSubtaskDetails() {
        List<Subtask> subtasks = subtaskRepository.findAll();  // Obtener todas las subtareas

        if (subtasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontraron subtareas activas"));
        }

        List<SubtaskDetailDTO> response = new ArrayList<>();

        for (Subtask s : subtasks) {
            if (!s.isActive()) {
                continue; // Si la subtarea está desactivada, la saltamos
            }

            ToDoItem task = s.getMainTask();

            SubtaskDetailDTO dto = new SubtaskDetailDTO();
            dto.id = s.getId();
            dto.title = s.getTitle();
            dto.estimatedHours = s.getEstimatedHours();
            dto.actualHours = s.getActualHours();
            dto.completed = s.isCompleted();
            dto.assignedDeveloperId = s.getAssignedDeveloperId();

            SubtaskDetailDTO.TaskInfo taskInfo = new SubtaskDetailDTO.TaskInfo();
            taskInfo.title = task.getTitle();
            taskInfo.description = task.getDescription();

            if (task.getSprint() != null) {
                Sprint sprint = task.getSprint();
                SubtaskDetailDTO.SprintInfo sprintInfo = new SubtaskDetailDTO.SprintInfo();
                sprintInfo.id = sprint.getId();
                sprintInfo.sprintNumber = sprint.getSprintNumber();
                sprintInfo.startDate = sprint.getStartDate();
                sprintInfo.endDate = sprint.getEndDate();
                taskInfo.sprint = sprintInfo;
            }

            dto.task = taskInfo;
            response.add(dto);  // Agregar los detalles de la subtarea a la respuesta
        }

        return ResponseEntity.ok(response);  // Devolver todas las subtareas con sus detalles
    }


}
