package com.springboot.MyTodoList.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DeveloperSprintStatsId implements Serializable {

    private Long developerId;
    private Long sprintId;

    // Getters y Setters
    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DeveloperSprintStatsId that = (DeveloperSprintStatsId) obj;
        return developerId.equals(that.developerId) && sprintId.equals(that.sprintId);
    }

    @Override
    public int hashCode() {
        return 31 * developerId.hashCode() + sprintId.hashCode();
    }
}
