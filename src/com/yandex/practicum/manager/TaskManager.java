package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;

import java.util.List;

public interface TaskManager {
    /* Task */
    void createTask(Task task);

    void updateTask(Task task);

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    /* Epic */
    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void checkStatus(Epic epic);

    List<Epic> getAllEpics();

    void deleteAllEpicsAndSubTasks();

    Epic getEpicById(int id);

    void deleteEpicById(int id);

    List<SubTask> getEpicSubTasks(Epic epic);

    /* SubTask */
    void updateSubTask(SubTask task);

    SubTask createSubTask(SubTask subtask);

    List<SubTask> getAllSubTasks();

    void deleteAllEpicSubTasks(Epic epic);

    void deleteAllSubTasks();

    SubTask getSubTaskById(int id);

    void deleteSubTaskById(int id);

    void setIdCounter(int maxId);
}
