package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public HistoryManager historyManager = Managers.getDefaultHistory();
    protected Map<Integer, Task> taskMap = new HashMap<>();
    protected Map<Integer, SubTask> subtaskMap = new HashMap<>();
    protected Map<Integer, Epic> epicMap = new HashMap<>();
    private int idCounter = 0;

    public int getNextId() {
        return idCounter++;
    }

    /* Task */
    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : taskMap.values()) {
            tasks.add(task);
            historyManager.addTaskToHistory(task);
        }
        return tasks;
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : taskMap.keySet()) {
            historyManager.remove(id);
        }
        taskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task == null) {
            return null;
        }
        historyManager.addTaskToHistory(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        historyManager.remove(id);
        taskMap.remove(id);
    }

    /* Epic */
    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        checkStatus(epic);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void checkStatus(Epic epic) {
        List<SubTask> subtasks = epic.getSubTasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        //проверка на хотя бы одну задачу IN_PROGRESS
        //проверка, все ли подзадачи со статусом DONE, NEW
        int tasksDone = 0;
        int tasksNew = 0;
        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }

            if (subtask.getStatus() == TaskStatus.DONE) {
                tasksDone++;
            } else if (subtask.getStatus() == TaskStatus.NEW) {
                tasksNew++;
            }
        }

        if (tasksDone == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (tasksNew == subtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            epics.add(epic);
            historyManager.addTaskToHistory(epic);
        }
        return epics;
    }

    @Override
    public void deleteAllEpicsAndSubTasks() {
        for (Integer id : subtaskMap.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : epicMap.keySet()) {
            historyManager.remove(id);
        }
        subtaskMap.clear();
        epicMap.clear();

    }

    @Override
    public Epic getEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            return null;
        }

        Epic epic = epicMap.get(id);
        historyManager.addTaskToHistory(epic);
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            return;
        }
        Epic epicToDelete = epicMap.get(id);
        List<SubTask> subTasksToDelete = epicToDelete.getSubTasks();
        for (SubTask subtask : subTasksToDelete) {
            historyManager.remove(subtask.getId());
            subtaskMap.remove(subtask.getId());
        }
        subTasksToDelete.clear();
        historyManager.remove(id);
        epicMap.remove(id);
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            historyManager.addTaskToHistory(subTask);
        }

        return epic.getSubTasks();
    }

    /* SubTask */
    @Override
    public void updateSubTask(SubTask task) {
        subtaskMap.put(task.getId(), task);

        int epicId = subtaskMap.get(task.getId()).getEpic();
        Epic epic = epicMap.get(epicId);
        checkStatus(epic);
    }

    @Override
    public SubTask createSubTask(SubTask subtask) {
        subtask.setId(getNextId());
        if (subtask.getEpic() == subtask.getId()) {
            return null;
        }
        subtaskMap.put(subtask.getId(), subtask);

        Epic epic = epicMap.get(subtask.getEpic());

        List<SubTask> subtasks = epic.getSubTasks() == null ? new ArrayList<>() : epic.getSubTasks();
        subtasks.add(subtask);
        epic.setSubTasks(subtasks);
        checkStatus(epic);
        return subtask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subtasks = new ArrayList<>();
        for (SubTask subtask : subtaskMap.values()) {
            subtasks.add(subtask);
            historyManager.addTaskToHistory(subtask);
        }
        return subtasks;
    }

    @Override
    public void deleteAllEpicSubTasks(Epic epic) {
        if (!epicMap.containsValue(epic)) {
            return;
        }
        // при удалении всех подзадач эпика, статус пустого эпика становится NEW
        List<SubTask> subtasks = epic.getSubTasks();
        for (SubTask subtask : subtasks) {
            historyManager.remove(subtask.getId());
            subtaskMap.remove(subtask.getId());
        }
        epic.getSubTasks().clear();
        epic.setStatus(TaskStatus.NEW);
    }

    @Override
    public void deleteAllSubTasks() {
        // при удалении всех подзадач эпиков, статус пустых эпикох становится NEW
        Set<Integer> epicIds = new HashSet<>();
        for (SubTask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
            epicIds.add(subtask.getEpic());
        }
        for (Integer epicId : epicIds) {
            epicMap.get(epicId).setStatus(TaskStatus.NEW);
        }
        subtaskMap.clear();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (!subtaskMap.containsKey(id)) {
            return null;
        }

        SubTask subtask = subtaskMap.get(id);
        historyManager.addTaskToHistory(subtask);
        return subtask;
    }

    @Override
    public void deleteSubTaskById(int id) {
        // удаление подзадачи из эпика и subtaskMap
        if (!subtaskMap.containsKey(id)) {
            return;
        }
        SubTask subtaskToDelete = subtaskMap.get(id);
        int epicId = subtaskMap.get(id).getEpic();
        Epic epic = epicMap.get(epicId);
        epic.getSubTasks().remove(subtaskToDelete);
        historyManager.remove(id);
        subtaskMap.remove(id);
        checkStatus(epic);
    }

}
