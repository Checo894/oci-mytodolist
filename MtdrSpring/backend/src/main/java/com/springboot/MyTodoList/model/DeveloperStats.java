package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entity representing developer statistics in the system.
 */

@Entity
@Table(name = "DEVELOPER_STATS")
public class DeveloperStats {

    @Id
    @Column(name = "DEVELOPER_ID")
    private Long developerId;

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

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
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
