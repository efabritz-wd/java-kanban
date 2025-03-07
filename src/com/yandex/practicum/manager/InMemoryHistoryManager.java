package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected final List<Task> tasksHistory = new ArrayList<>();
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }

    @Override
    public void addTaskToHistory(Task task) {
        if(tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        Task taskToSave = new Task(task.getTitle(), task.getDescription());
        taskToSave.setId(task.getId());
        taskToSave.setStatus(task.getStatus());
        tasksHistory.add(taskToSave);
    }
}
