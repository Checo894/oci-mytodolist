package com.springboot.MyTodoList.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "TODOITEM")
public class ToDoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "TITLE", length = 100)
    private String title;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;
    
    @Column(name = "CREATION_TS", nullable = false, updatable = false)
    private OffsetDateTime creation_ts;
    
    @Column(name = "done")
    private boolean done;
    
    @Column(name = "START_DATE")
    private OffsetDateTime startDate;
    
    @Column(name = "END_DATE")
    private OffsetDateTime endDate;
    
    @Column(name = "PROGRESS")
    private double progress;
    
    @Column(name = "STATUS", length = 50)
    private String status;
    
    @OneToMany(mappedBy = "mainTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Evita serialización infinita
    private List<Subtask> subtasks;
    
    public ToDoItem() {
    }
    
    public ToDoItem(Long id, String title, String description, OffsetDateTime creation_ts, boolean done, OffsetDateTime startDate, OffsetDateTime endDate, double progress, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creation_ts = creation_ts;
        this.done = done;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progress = Math.min(progress, 999.99); // Limitar el progreso al máximo permitido
        this.status = validateStatus(status);
    }
    
    public Long getID() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }
    
    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }
    
    public boolean isDone() {
        return done;
    }
    
    public void setDone(boolean done) {
        this.done = done;
    }
    
    public OffsetDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }
    
    public OffsetDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public void setProgress(double progress) {
        this.progress = Math.min(progress, 999.99);
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = validateStatus(status);
    }
    
    public List<Subtask> getSubtasks() {
        return subtasks;
    }
    
    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }
    
    private String validateStatus(String status) {
        List<String> validStatuses = List.of("Not Started", "In Progress", "Completed", "Cancelled", "Incomplete");
        return validStatuses.contains(status) ? status : "Not Started";
    }
    
    @Override
    public String toString() {
        return "ToDoItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", done=" + done +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", progress=" + progress +
                ", status='" + status + '\'' +
                '}';
    }
}
