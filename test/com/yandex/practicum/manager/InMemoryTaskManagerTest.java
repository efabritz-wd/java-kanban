package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static TaskManager taskManager;

    @BeforeEach
    public void beforeAll() throws IOException {
        taskManager = Managers.getDefault();
        super.setupManager((InMemoryTaskManager) taskManager);
    }

    @Test
    public void epicStatusTest() {
        TaskManager taskManager = Managers.getDefault();
        Epic statusEpic = new Epic("Status epic", "this is status epic");
        taskManager.createEpic(statusEpic);

        SubTask statusSubtaskFirst = new SubTask("Subtask 1", "Description", statusEpic.getId());
        SubTask statusSubtaskSecond = new SubTask("Subtask 2", "Description", statusEpic.getId());
        statusSubtaskFirst.setStartTime(LocalDateTime.now());
        statusSubtaskFirst.setDuration(Duration.ofMinutes(20));
        statusSubtaskSecond.setStartTime(LocalDateTime.now().plusHours(1));
        statusSubtaskSecond.setDuration(Duration.ofHours(2));

        statusEpic.setSubTasks(Arrays.asList(statusSubtaskFirst, statusSubtaskSecond));
        assertEquals(TaskStatus.NEW, statusEpic.getStatus());

        statusSubtaskFirst.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(statusEpic);
        assertEquals(TaskStatus.IN_PROGRESS, statusEpic.getStatus());

        statusSubtaskSecond.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(statusEpic);
        assertEquals(TaskStatus.IN_PROGRESS, statusEpic.getStatus());

        statusSubtaskFirst.setStatus(TaskStatus.NEW);
        statusSubtaskSecond.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(statusEpic);
        assertEquals(TaskStatus.IN_PROGRESS, statusEpic.getStatus());

        statusSubtaskFirst.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(statusEpic);
        assertEquals(TaskStatus.DONE, statusEpic.getStatus());
    }

    @Test
    void getAndDeleteSubTaskById() {
        if (taskManager.getAllSubTasks().isEmpty()) {
            taskManager.createSubTask(subTaskFirst);
        }
        final SubTask subTask = taskManager.getAllSubTasks().get(0);
        final int subTaskId = subTask.getId();
        assertNotNull(taskManager.getSubTaskById(subTaskId), "Подзадача не найдена.");

        taskManager.deleteSubTaskById(subTaskId);
        assertNull(taskManager.getSubTaskById(subTaskId), "Подзадача найдена.");
    }

    @Test
    void taskConsistency() {
        Task newTask = new Task("First task 2", "Description 2");
        newTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createTask(newTask);
        int newTaskId = newTask.getId();

        assertEquals(newTask.getTitle(), taskManager.getTaskById(newTaskId).getTitle());
        assertEquals(newTask.getDescription(), taskManager.getTaskById(newTaskId).getDescription());
        assertEquals(newTask.getStatus(), taskManager.getTaskById(newTaskId).getStatus());
    }

    @Test
    void settersInsecurity() {
        Task newTask = new Task("Example task", "Description");
        newTask.setId(100);
        //удаление всех возможно созданных задач, эпиков и подзадач
        clearTasks();
        taskManager.createTask(newTask);
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicsAndSubTasks();
        taskManager.createTask(newTask);
        List<Task> taskList = taskManager.getAllTasks();
        assertNull(taskManager.getTaskById(100));
        //возможный некорректный функионал
        if (!taskList.isEmpty()) {
            Task task = taskList.get(0);
            task.setId(100);
            taskManager.updateTask(task);
            assertNotNull(taskManager.getTaskById(100));
        }
    }

    @Test
    void taskTimeCrossingTest() {
        Task taskFirst = new Task("1 задача", "описание 1");
        Task taskSecond = new Task("2 задача", "описание 2");
        taskFirst.setStartTime(LocalDateTime.now().plusHours(1));
        taskFirst.setDuration(Duration.ofMinutes(30));

        taskSecond.setStartTime(LocalDateTime.now().plusHours(1).plusMinutes(20));
        taskSecond.setDuration(Duration.ofMinutes(10));
        taskManager.createTask(taskFirst);
        assertTrue(((InMemoryTaskManager) taskManager).checkTaskTimeCrossing(taskSecond));
        taskManager.createTask(taskSecond);

        Epic epic = new Epic("1 эпик", "Описание");
        taskManager.createEpic(epic);

        SubTask subtaskFirst = new SubTask("1 подзадача", "описание", epic.getId());
        SubTask subTaskSecond = new SubTask("2 подзадача", "описание", epic.getId());
        subtaskFirst.setStartTime(LocalDateTime.now().plusHours(2));
        subtaskFirst.setDuration(Duration.ofMinutes(10));

        subTaskSecond.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(30));
        subTaskSecond.setDuration(Duration.ofMinutes(10));

        taskManager.createSubTask(subtaskFirst);
        assertFalse(((InMemoryTaskManager) taskManager).checkTaskTimeCrossing(subTaskSecond));
        taskManager.createSubTask(subTaskSecond);

        assertFalse(taskManager.getAllTasks().contains(taskSecond));
        assertTrue(taskManager.getAllSubTasks().contains(subTaskSecond));
    }
}