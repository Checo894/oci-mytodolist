// src/main/java/com/springboot/MyTodoList/dto/SubtaskDetailDTO.java
package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

public class SubtaskDetailDTO {
    public Long id;
    public String title;
    public Double estimatedHours;
    public Double actualHours;
    public Boolean completed;
    public TaskInfo task;

    public static class TaskInfo {
        public String title;
        public String description;
        public SprintInfo sprint;
        public String status;
        public double progress;
    }

    public static class SprintInfo {
        public Integer sprintNumber;
        public Long id;
        public LocalDate startDate;
        public LocalDate endDate;
    }
}
