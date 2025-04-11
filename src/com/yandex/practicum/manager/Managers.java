package com.yandex.practicum.manager;

import java.io.File;

public class Managers {

    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static FileBackedTaskManager backedManager;

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    public static TaskManager getDefaultBackup(File file) {
        if (backedManager == null) {
            backedManager = new FileBackedTaskManager(file);
        }
        return backedManager;
    }
}
