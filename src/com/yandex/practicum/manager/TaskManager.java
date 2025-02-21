package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager {
    private int idCounter = 0;

    protected HashMap<Integer, Task> taskMap = new HashMap<>();
    protected HashMap<Integer, SubTask> subtaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicMap = new HashMap<>();

    public int getNextId() {
        return idCounter++;
    }

    /* Task */
    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
        System.out.println("Задача добавлена!");
    }

    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
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
    public void createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        System.out.println("Эпик без задач добавлен!");
    }

    public void updateEpic(Epic epic) {
        checkStatus(epic);
        epicMap.put(epic.getId(), epic);
        System.out.println("Эпик " + epic + " обновлен.");
    }

    public void checkStatus(Epic epic) {
        ArrayList<SubTask> subtasks = epic.getSubTasks();
        if(subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        //проверка на хотя бы одну задачу IN_PROGRESS
        //проверка, все ли подзадачи со статусом DONE, NEW
        int tasksDone = 0;
        int tasksNew = 0;
        for(SubTask subtask: subtasks) {
            if(subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }

            if(subtask.getStatus() == TaskStatus.DONE) {
                tasksDone++;
            } else if(subtask.getStatus() == TaskStatus.NEW) {
                tasksNew++;
            }
        }

        if(tasksDone == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if(tasksNew == subtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
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
    public void updateSubTask(SubTask task) {
        subtaskMap.put(task.getId(), task);

        int epicId = subtaskMap.get(task.getId()).getEpic();
        Epic epic = epicMap.get(epicId);
        checkStatus(epic);

        System.out.println("Подзадача " + task + " обновлена.");
    }

    public SubTask createSubTask(SubTask subtask) {

        subtaskMap.put(subtask.getId(), subtask);
        System.out.println("Подзадача добавлена!");

        Epic epic = epicMap.get(subtask.getEpic());

        ArrayList<SubTask> subtasks = epic.getSubTasks() == null ? new ArrayList<>() : epic.getSubTasks();
        subtasks.add(subtask);
        epic.setSubTasks(subtasks);
        checkStatus(epic);
        return subtask;
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
