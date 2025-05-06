package com.yandex.practicum.utils;

import com.yandex.practicum.tasks.Task;

import java.util.Comparator;

public class TaskPriorityComparator implements Comparator<Task> {
    @Override
    public int compare(Task task, Task otherTask) {
        return task.getStartTime().compareTo(otherTask.getStartTime());
    }
}