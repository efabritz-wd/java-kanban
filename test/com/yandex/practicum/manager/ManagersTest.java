package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskmanagerFirst = Managers.getDefault();
        TaskManager taskmanagerSecond = Managers.getDefault();

        assertNotNull(taskmanagerFirst);
        assertNotNull(taskmanagerSecond);
        assertEquals(taskmanagerFirst, taskmanagerSecond);

        taskmanagerFirst.createTask(new Task("title", "description"));
        assertNotNull(taskmanagerFirst.getAllTasks(), "Задача не создана");
        assertEquals(1, taskmanagerFirst.getAllTasks().size());
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManagerFirst = Managers.getDefaultHistory();
        HistoryManager historyManagerSecond = Managers.getDefaultHistory();

        assertNotNull(historyManagerFirst);
        assertNotNull(historyManagerSecond);
        assertEquals(historyManagerFirst, historyManagerSecond);

        historyManagerFirst.addTaskToHistory(new Task("title", "description"));
        assertNotNull(historyManagerFirst.getHistory(), "История не создана");
    }
}