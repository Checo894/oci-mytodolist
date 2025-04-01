package com.springboot.MyTodoList.util;

import java.time.LocalDate;

public class SprintCreationSession {
    public enum State {
        WAITING_NUMBER,
        WAITING_START_DATE,
        WAITING_END_DATE
    }

    public Integer sprintNumber;
    public LocalDate startDate;
    public LocalDate endDate;
    public State state;
}
