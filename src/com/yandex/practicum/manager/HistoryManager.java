package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addTaskToHistory(Task task);

    void remove(int id);

    void clearHistory();
}
