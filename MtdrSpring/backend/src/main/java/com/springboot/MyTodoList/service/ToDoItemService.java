package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
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

    @Autowired
    private SprintRepository sprintRepository;


    public List<ToDoItem> findAll() {
        return toDoItemRepository.findByIsActiveTrue(); // 🔄 solo activos
    }

    public ResponseEntity<ToDoItem> getItemById(Long id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent() && todoData.get().isActive()) { // 🔄 verificación
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        if (toDoItem.getCreation_ts() == null) {
            toDoItem.setCreation_ts(OffsetDateTime.now());
        }
        if (toDoItem.getStartDate() == null) {
            toDoItem.setStartDate(OffsetDateTime.now());
        }
    
        // 🟨 Asociar Sprint si se mandó ID válido
        if (toDoItem.getSprint() != null && toDoItem.getSprint().getId() != null) {
            sprintRepository.findById(toDoItem.getSprint().getId()).ifPresent(toDoItem::setSprint);
        }
    
        toDoItem.setProgress(Math.min(toDoItem.getProgress(), 999.99));
        toDoItem.setStatus(validateStatus(toDoItem.getStatus()));
        toDoItem.setActive(true);
        return toDoItemRepository.save(toDoItem);
    }
    

    public boolean deleteToDoItem(Long id) {
        Optional<ToDoItem> toDoItem = toDoItemRepository.findById(id);
        if (toDoItem.isPresent() && toDoItem.get().isActive()) {
            ToDoItem item = toDoItem.get();
            item.setActive(false); // 🔄 borrado lógico
            toDoItemRepository.save(item);
            return true;
        }
        return false;
    }

    public ToDoItem updateToDoItem(Long id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent() && toDoItemData.get().isActive()) { // 🔄 validación
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
            if (td.getSprint() != null && td.getSprint().getId() != null) {
                sprintRepository.findById(td.getSprint().getId()).ifPresent(toDoItem::setSprint);
            }            
            updateProgressAndStatus(toDoItem);
            return toDoItemRepository.save(toDoItem);
        }
        return null;
    }

    private void updateProgressAndStatus(ToDoItem toDoItem) {
        List<Subtask> subtasks = subtaskRepository.findByMainTaskIdAndIsActiveTrue(toDoItem.getID()); // 🔄 usar solo activas
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

    public List<ToDoItem> getAllItemsIncludingInactive() {
        return toDoItemRepository.findAll();
    }
    
}
