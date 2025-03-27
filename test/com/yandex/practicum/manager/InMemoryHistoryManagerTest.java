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
        historyManager.getHistory().clear();
        Task task = new Task("task", "description");
        taskManager.createTask(task);
        historyManager.addTaskToHistory(task);
        List<Task> taskList = historyManager.getHistory();
        assertFalse(taskList.isEmpty());

        historyManager.remove(task.getId());
        List<Task> taskListEmpty = historyManager.getHistory();

        assertTrue(taskListEmpty.isEmpty(), "История задач не пуста." + taskListEmpty);
    }

}