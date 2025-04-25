package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    public static HistoryManager historyManager;
    public static TaskManager taskManager;
    public static Task taskFirst;


    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskFirst = new Task("First task", "Description 1");
    }

    @Test
    void addTaskToHistory() {
        if (taskManager.getAllTasks().isEmpty()) {
            taskManager.createTask(taskFirst);
        }
        historyManager.addTaskToHistory(taskFirst);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        Task lastTask = history.getLast();
        assertEquals(taskFirst, lastTask);
    }

    @Test
    void checkHistory() {
        taskManager.createTask(taskFirst);
        if (historyManager.getHistory().isEmpty()) {
            historyManager.addTaskToHistory(taskFirst);
        }

        taskFirst.setStatus(TaskStatus.DONE);
        taskManager.updateTask(taskFirst);
        historyManager.addTaskToHistory(taskFirst);

        final List<Task> history = historyManager.getHistory();
        assertEquals(TaskStatus.DONE, history.getLast().getStatus());
    }

    @Test
    void removeFromHistory() {
        historyManager.clearHistory();
        Task task1 = new Task("task1", "description");
        Task task2 = new Task("task2", "description");
        Task task3 = new Task("task3", "description");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task1);

        historyManager.addTaskToHistory(task2);
        historyManager.addTaskToHistory(task3);

        assertEquals(3, historyManager.getHistory().size());

        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task2));

        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task1));

        historyManager.remove(task3.getId());
        assertEquals(0, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task3));
    }

}