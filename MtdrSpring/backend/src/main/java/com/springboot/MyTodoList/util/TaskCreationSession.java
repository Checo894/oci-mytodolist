package com.springboot.MyTodoList.util;

import com.springboot.MyTodoList.model.Subtask;
import java.util.ArrayList;
import java.util.List;

public class TaskCreationSession {
    public String title;
    public String description;
    public Long sprintId;
    public List<Subtask> subtasks = new ArrayList<>();
    public Subtask currentSubtask = new Subtask();
    public int expectedSubtaskCount;
    public int currentSubtaskIndex = 0;
    public State state;

    public enum State {
        WAITING_TITLE,
        WAITING_DESCRIPTION,
        WAITING_SPRINT,
        WAITING_SUBTASK_COUNT,
        WAITING_SUBTASK_TITLE,
        WAITING_SUBTASK_HOURS,
        WAITING_SUBTASK_DEVELOPER
    }
}
