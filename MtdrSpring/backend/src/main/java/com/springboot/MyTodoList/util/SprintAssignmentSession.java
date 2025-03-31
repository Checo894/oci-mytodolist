package com.springboot.MyTodoList.util;

public class SprintAssignmentSession {
    public enum State {
        WAITING_TASK_SELECTION,
        WAITING_SPRINT_ID
    }

    public Long selectedTaskId;
    public State state;
}
