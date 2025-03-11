package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    @Autowired
    private SubtaskRepository subtaskRepository;
    
    public List<ToDoItem> findAll() {
        return toDoItemRepository.findAll();
    }
    
    public ResponseEntity<ToDoItem> getItemById(Long id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        return todoData.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        if (toDoItem.getCreation_ts() == null) {
            toDoItem.setCreation_ts(OffsetDateTime.now());
        }
        if (toDoItem.getStartDate() == null) {
            toDoItem.setStartDate(OffsetDateTime.now());
        }
        toDoItem.setProgress(Math.min(toDoItem.getProgress(), 999.99));
        toDoItem.setStatus(validateStatus(toDoItem.getStatus()));
        return toDoItemRepository.save(toDoItem);
    }
    
    public boolean deleteToDoItem(Long id) {
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public ToDoItem updateToDoItem(Long id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setId(id); 
            toDoItem.setTitle(td.getTitle());
            toDoItem.setCreation_ts(td.getCreation_ts());
            toDoItem.setDescription(td.getDescription());
            toDoItem.setDone(td.isDone());
            toDoItem.setStartDate(td.getStartDate());
            toDoItem.setEndDate(td.getEndDate());
            toDoItem.setProgress(Math.min(td.getProgress(), 999.99));
            toDoItem.setStatus(validateStatus(td.getStatus()));
            
            // Recalcular progreso y estado
            updateProgressAndStatus(toDoItem);
            
            return toDoItemRepository.save(toDoItem);
        }
        return null;
    }
    
    private void updateProgressAndStatus(ToDoItem toDoItem) {
        List<Subtask> subtasks = subtaskRepository.findByMainTaskId((long) toDoItem.getID());
        if (subtasks.isEmpty()) {
            toDoItem.setProgress(0.0);
            toDoItem.setStatus("Not Started");
            return;
        }

        long completedSubtasks = subtasks.stream().filter(Subtask::isCompleted).count();
        double progress = (double) completedSubtasks / subtasks.size() * 100;
        toDoItem.setProgress(Math.min(progress, 999.99));
        
        if (completedSubtasks == 0) {
            toDoItem.setStatus("Not Started");
        } else if (completedSubtasks == subtasks.size()) {
            toDoItem.setStatus("Completed");
        } else if (toDoItem.getEndDate() != null && toDoItem.getEndDate().isBefore(OffsetDateTime.now())) {
            toDoItem.setStatus("Incomplete");
        } else {
            toDoItem.setStatus("In Progress");
        }
    }
    
    private String validateStatus(String status) {
        List<String> validStatuses = List.of("Not Started", "In Progress", "Completed", "Cancelled", "Incomplete");
        return validStatuses.contains(status) ? status : "Not Started";
    }
}
