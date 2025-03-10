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
    @JsonBackReference  // Evita la serialización infinita
    private ToDoItem mainTask;

    @Column(length = 255, nullable = false)
    private String title;

    private boolean completed;

    @Column(name = "ASSIGNED_DEVELOPER_ID")
    private Long assignedDeveloperId;

    public Subtask() {}

    public Subtask(Long id, ToDoItem mainTask, String title, boolean completed, Long assignedDeveloperId) {
        this.id = id;
        this.mainTask = mainTask;
        this.title = title;
        this.completed = completed;
        this.assignedDeveloperId = assignedDeveloperId;
    }

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

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", mainTaskId=" + (mainTask != null ? mainTask.getID() : "null") +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", assignedDeveloperId=" + assignedDeveloperId +
                '}';
    }
}
