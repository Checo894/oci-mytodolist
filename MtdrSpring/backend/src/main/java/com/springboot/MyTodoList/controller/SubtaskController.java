package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.service.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtasks")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

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
}
