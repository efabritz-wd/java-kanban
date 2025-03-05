package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    /* Task */
    void createTask(Task task);

    void updateTask(Task task);

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    /* Epic */
    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void checkStatus(Epic epic);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpicsAndSubTasks();

    Epic getEpicById(int id);

    void deleteEpicById(int id);

    ArrayList<SubTask> getEpicSubTasks(Epic epic);

    /* SubTask */
    void updateSubTask(SubTask task);

    SubTask createSubTask(SubTask subtask);

    ArrayList<SubTask> getAllSubTasks();

    void deleteAllEpicSubTasks(Epic epic);

    void deleteAllSubTasks();

    SubTask getSubTaskById(int id);

    void deleteSubTaskById(int id);
}
