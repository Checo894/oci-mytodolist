package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todolist")
public class ToDoItemController {
    
    @Autowired
    private ToDoItemService toDoItemService;
    
    @GetMapping
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable Long id) {
        ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
        return responseEntity.getStatusCode() == HttpStatus.OK ?
                new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping
    public ResponseEntity<?> addToDoItem(@RequestBody ToDoItem todoItem) {
        try {
            ToDoItem td = toDoItemService.addToDoItem(todoItem);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", "/todolist/" + td.getID());
            responseHeaders.set("Access-Control-Expose-Headers", "location");
            return ResponseEntity.ok().headers(responseHeaders).body(td);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al agregar la tarea: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable Long id) {
        try {
            ToDoItem updatedItem = toDoItemService.updateToDoItem(id, toDoItem);
            return updatedItem != null ?
                    new ResponseEntity<>(updatedItem, HttpStatus.OK) :
                    new ResponseEntity<>("Tarea no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la tarea: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteToDoItem(@PathVariable("id") Long id) {
        try {
            boolean flag = toDoItemService.deleteToDoItem(id);
            return flag ? new ResponseEntity<>("Tarea eliminada correctamente", HttpStatus.OK)
                        : new ResponseEntity<>("Tarea no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la tarea: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getToDoItemsByStatus(@PathVariable String status) {
        List<ToDoItem> items = toDoItemService.findAll().stream()
                .filter(item -> item.getStatus().equalsIgnoreCase(status))
                .toList();
        return items.isEmpty() ?
                new ResponseEntity<>("No se encontraron tareas con el estado: " + status, HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(items, HttpStatus.OK);
    }
}
