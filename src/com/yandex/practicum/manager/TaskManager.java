package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager {
    private int idCounter = 0;

    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, SubTask> subtaskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();

    /* Task */
    public Task createTask(String title, String description) {
        if(title == null) {
            System.out.println("Невозможно создать задачу без названия!");
            return null;
        }
        if(description == null) {
            System.out.println("Задача без описания!");
            Task task = new Task(++idCounter, title);
            taskMap.put(task.getId(), task);
            System.out.println("Задача без описания добавлена!");
            return task;
        } else {
            Task task = new Task(++idCounter, title, description);
            taskMap.put(task.getId(), task);
            System.out.println("Задача добавлена!");
            return task;
        }
    }

    public void updateTask(Task task, int taskId) {
        if(!taskMap.containsKey(taskId)) {
            System.out.println("Задачи с id: " + taskId + " не существует!");
            return;
        }

        taskMap.put(taskId, task);
        System.out.println("Задача " + task + " обновлена.");
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for(Task task : taskMap.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public Task getTaskById(int id) {
        if(!taskMap.containsKey(id)) {
            System.out.println("Задачи с id: " + id + " не существует!");
            return null;
        }
        return taskMap.get(id);
    }

    public void deleteTaskById(int id) {
        if(!taskMap.containsKey(id)) {
            System.out.println("Задачи с id: " + id + " не существует!");
            return;
        }
        taskMap.remove(id);
    }

    /* Epic */
    public Epic createEpic(String title) {
        if(title == null) {
            System.out.println("Невозможно создать эпик без названия!");
            return null;
        }

        Epic epic = new Epic(++idCounter, title);
        epicMap.put(epic.getId(), epic);
        System.out.println("Эпик без задач добавлен!");
        return epic;

    }

    public void updateEpic(Epic epic, int epicId) {
        if(!epicMap.containsKey(epicId)) {
            System.out.println("Эпика с id: " + epicId + " не существует!");
            return;
        }
        checkStatus(epic);
        epicMap.put(epicId, epic);
        System.out.println("Эпик " + epic + " обновлен.");
    }

    public void checkStatus(Epic epic) {
        ArrayList<SubTask> subtasks = epic.getSubTasks();
        if(subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        //проверка, все ли подзадачи со статусом DONE
        boolean allDone = true;
        for(SubTask subtask: subtasks) {
            if(subtask.getStatus() == TaskStatus.DONE) {
                allDone = allDone && true;
            } else {
                allDone = allDone && false;
            }
        }

        if(allDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        //проверка на хотя бы одну задачу IN_PROGRESS
        for(SubTask subtask: subtasks) {
            if(subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
        }
    }
    
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for(Epic epic : epicMap.values()) {
            epics.add(epic);
        }
        return epics;
    }

    public void deleteAllEpicsAndSubTasks() {
        subtaskMap.clear();
        epicMap.clear();
    }

    public Epic getEpicById(int id) {
        if(!epicMap.containsKey(id)) {
            System.out.println("Эпика с id: " + id + " не существует!");
            return null;
        }
        return epicMap.get(id);
    }

    public void deleteEpicById(int id) {
        if(!epicMap.containsKey(id)) {
            System.out.println("Эпика с id: " + id + " не существует!");
            return;
        }
        Epic epicToDelete = epicMap.get(id);
        ArrayList<SubTask> subTasksToDelete = epicToDelete.getSubTasks();
        for(SubTask subtask : subTasksToDelete) {
            subtaskMap.remove(subtask.getId());
        }
        subTasksToDelete.clear();
        epicMap.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    /* SubTask */
    public void updateSubTask(SubTask task, int subtaskId) {
        if(!subtaskMap.containsKey(subtaskId)) {
            System.out.println("Подзадачи с id: " + subtaskId + " не существует!");
            return;
        }

        subtaskMap.put(subtaskId, task);

        int epicId = subtaskMap.get(subtaskId).getEpic();
        Epic epic = epicMap.get(epicId);
        checkStatus(epic);

        System.out.println("Подзадача " + task + " обновлена.");
    }

    public SubTask createSubTask(String title, String description, int epicId) {
        if(!epicMap.containsKey(epicId)) {
            System.out.println("Эпика с id: " + epicId + " не существует!" + "\'"
                    + "Невозможно создать подзадачу");
            return null;
        }

        if(title == null) {
            System.out.println("Невозможно создать подзадачу без названия!");
            return null;
        }
        SubTask taskCreated = null;

        if(description == null) {
            System.out.println("Подзадача без описания!");
            SubTask task = new SubTask(++idCounter, title, epicId);
            subtaskMap.put(task.getId(), task);
            taskCreated = task;
            System.out.println("Подзадача без описания добавлена!");
        } else {
            SubTask task = new SubTask(++idCounter, title, description, epicId);
            subtaskMap.put(task.getId(), task);
            System.out.println("Подзадача добавлена!");
            taskCreated = task;
        }
        Epic epic = epicMap.get(epicId);

        ArrayList<SubTask> subtasks = epic.getSubTasks() == null ? new ArrayList<>() : epic.getSubTasks();
        subtasks.add(taskCreated);
        epic.setSubTasks(subtasks);
        checkStatus(epic);
        return taskCreated;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subtasks = new ArrayList<>();
        for(SubTask subtask : subtaskMap.values()) {
            subtasks.add(subtask);
        }
        return subtasks;
    }

    public void deleteAllEpicSubTasks(Epic epic) {
        if(!epicMap.containsValue(epic)) {
            System.out.println("Эпика с не существует! Удаление подзадач невозможно.");
            return;
        }
        // при удалении всех подзадач эпика, статус пустого эпика становится NEW
        ArrayList<SubTask> subtasks = epic.getSubTasks();
        for(SubTask subtask : subtasks) {
            subtaskMap.remove(subtask.getId());
        }
        epic.getSubTasks().clear();
        epic.setStatus(TaskStatus.NEW);
    }

    public void deleteAllSubTasks() {
        // при удалении всех подзадач эпиков, статус пустых эпикох становится NEW
        Set<Integer> epicIds = new HashSet<>();
        for(SubTask subtask : subtaskMap.values()) {
            epicIds.add(subtask.getEpic());
        }
        for(Integer epicId : epicIds) {
            epicMap.get(epicId).setStatus(TaskStatus.NEW);
        }
        subtaskMap.clear();
    }

    public SubTask getSubTaskById(int id) {
        if(!subtaskMap.containsKey(id)) {
            System.out.println("Подзадачи с id: " + id + " не существует!");
            return null;
        }
        return subtaskMap.get(id);
    }

    public void deleteSubTaskById(int id) {
        // удаление подзадачи из эпика и subtaskMap
        if(!subtaskMap.containsKey(id)) {
            System.out.println("Подзадачи с id: " + id + " не существует!");
            return;
        }
        SubTask subtaskToDelete = subtaskMap.get(id);
        int epicId = subtaskMap.get(id).getEpic();
        Epic epic = epicMap.get(epicId);
        epic.getSubTasks().remove(subtaskToDelete);
        subtaskMap.remove(id);
        checkStatus(epic);
    }
    
}
