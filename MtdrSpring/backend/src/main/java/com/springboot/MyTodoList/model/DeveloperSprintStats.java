package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "DEVELOPER_SPRINT_STATS")
public class DeveloperSprintStats {

    @EmbeddedId
    private DeveloperSprintStatsId id;

    @ManyToOne
    @MapsId("developerId")
    @JoinColumn(name = "DEVELOPER_ID", nullable = false)
    private Developer developer;

    @ManyToOne
    @MapsId("sprintId")
    @JoinColumn(name = "SPRINT_ID", nullable = false)
    private Sprint sprint;

    @Column(name = "TOTAL_ASSIGNED_COUNT")
    private Integer totalAssignedCount;

    @Column(name = "TOTAL_COMPLETED_COUNT")
    private Integer totalCompletedCount;

    @Column(name = "SUM_ESTIMATED_HOURS")
    private Double sumEstimatedHours;

    @Column(name = "SUM_ACTUAL_HOURS")
    private Double sumActualHours;

    @Column(name = "LAST_UPDATED_TS")
    private OffsetDateTime lastUpdatedTs;

    // Getters y setters
    public DeveloperSprintStatsId getId() {
        return id;
    }

    public void setId(DeveloperSprintStatsId id) {
        this.id = id;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Integer getTotalAssignedCount() {
        return totalAssignedCount;
    }

    public void setTotalAssignedCount(Integer totalAssignedCount) {
        this.totalAssignedCount = totalAssignedCount;
    }

    public Integer getTotalCompletedCount() {
        return totalCompletedCount;
    }

    public void setTotalCompletedCount(Integer totalCompletedCount) {
        this.totalCompletedCount = totalCompletedCount;
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
