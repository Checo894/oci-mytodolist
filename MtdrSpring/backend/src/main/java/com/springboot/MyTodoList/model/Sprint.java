// Sprint.java
package com.springboot.MyTodoList.model;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SPRINTS")
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SPRINT_NUMBER", nullable = false)
    private int sprintNumber;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    protected Sprint() {
        // for JPA
    }

    public Sprint(int sprintNumber, LocalDate startDate, LocalDate endDate) {
        this.sprintNumber = sprintNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public int getSprintNumber() {
        return sprintNumber;
    }

    public void setSprintNumber(int sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format(
            "Sprint{id=%d, number=%d, start=%s, end=%s}",
            id, sprintNumber, startDate, endDate
        );
    }
}
