package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "SPRINT_STATS")
public class SprintStats {

    @Id
    @Column(name = "SPRINT_ID")
    private Long sprintId;

    @Column(name = "TOTAL_SUBTASKS")
    private Integer totalSubtasks;

    @Column(name = "TOTAL_COMPLETED")
    private Integer totalCompleted;

    @Column(name = "SUM_ESTIMATED_HOURS")
    private Double sumEstimatedHours;

    @Column(name = "SUM_ACTUAL_HOURS")
    private Double sumActualHours;

    @Column(name = "LAST_UPDATED_TS")
    private OffsetDateTime lastUpdatedTs;

    // Getters y setters

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Integer getTotalSubtasks() {
        return totalSubtasks;
    }

    public void setTotalSubtasks(Integer totalSubtasks) {
        this.totalSubtasks = totalSubtasks;
    }

    public Integer getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(Integer totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public Double getSumEstimatedHours() {
        return sumEstimatedHours;
    }

    public void setSumEstimatedHours(Double sumEstimatedHours) {
        this.sumEstimatedHours = sumEstimatedHours;
    }

    public Double getSumActualHours() {
        return sumActualHours;
    }

    public void setSumActualHours(Double sumActualHours) {
        this.sumActualHours = sumActualHours;
    }

    public OffsetDateTime getLastUpdatedTs() {
        return lastUpdatedTs;
    }

    public void setLastUpdatedTs(OffsetDateTime lastUpdatedTs) {
        this.lastUpdatedTs = lastUpdatedTs;
    }
}
