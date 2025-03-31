package com.springboot.MyTodoList.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

@Entity
@Table(name = "SUBTASKS")
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MAIN_TASK_ID", nullable = false)
    @JsonBackReference
    private ToDoItem mainTask;

    @Column(length = 255, nullable = false)
    private String title;

    private boolean completed;

    @Column(name = "ASSIGNED_DEVELOPER_ID")
    private Long assignedDeveloperId;

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "ESTIMATED_HOURS")
    private Double estimatedHours;

    @Column(name = "ACTUAL_HOURS")
    private Double actualHours;

    public Subtask() {}

    public Subtask(Long id, ToDoItem mainTask, String title, boolean completed, Long assignedDeveloperId, boolean isActive, Double estimatedHours, Double actualHours) {
        this.id = id;
        this.mainTask = mainTask;
        this.title = title;
        this.completed = completed;
        this.assignedDeveloperId = assignedDeveloperId;
        this.isActive = isActive;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ToDoItem getMainTask() {
        return mainTask;
    }

    public void setMainTask(ToDoItem mainTask) {
        this.mainTask = mainTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getAssignedDeveloperId() {
        return assignedDeveloperId;
    }

    public void setAssignedDeveloperId(Long assignedDeveloperId) {
        this.assignedDeveloperId = assignedDeveloperId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", mainTaskId=" + (mainTask != null ? mainTask.getID() : "null") +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", assignedDeveloperId=" + assignedDeveloperId +
                ", isActive=" + isActive +
                ", estimatedHours=" + estimatedHours +
                ", actualHours=" + actualHours +
                '}';
    }
}
