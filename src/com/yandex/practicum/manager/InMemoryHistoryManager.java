package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected List<Task> tasksHistory = new ArrayList<>();
    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addTaskToHistory(Task task) {
        if(tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
