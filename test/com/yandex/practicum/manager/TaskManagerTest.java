package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public static Task taskFirst;
    public static Task taskSecond;
    public static Epic epicFirst;
    public static Epic epicSecond;
    public static SubTask subTaskFirst;
    public static SubTask subTaskSecond;
    public static SubTask subTaskThird;
    public static SubTask subTaskFourth;
    public static int epicIdSecond;
    public T taskManager;

    public void setupManager(T taskManager) throws IOException {
        this.taskManager = taskManager;

        epicFirst = new Epic("First epic", "def");
        epicSecond = new Epic("Second epic", "def");

        taskManager.createEpic(epicFirst);
        taskManager.createEpic(epicSecond);

        subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());
        subTaskFirst.setStartTime(LocalDateTime.now().plusMinutes(10));
        subTaskFirst.setDuration(Duration.ofMinutes(20));
        subTaskSecond.setStartTime(LocalDateTime.now().plusMinutes(40));
        subTaskSecond.setDuration(Duration.ofMinutes(10));

        epicIdSecond = epicSecond.getId();
        subTaskThird = new SubTask("Subtask 3", "Description", epicIdSecond);
        subTaskFourth = new SubTask("Subtask 4", "Description", epicIdSecond);

        subTaskFourth.setStartTime(LocalDateTime.now().plusHours(1).plusMinutes(10));
        subTaskFourth.setDuration(Duration.ofMinutes(20));
        subTaskThird.setStartTime(LocalDateTime.now().plusHours(1).plusMinutes(40));
        subTaskThird.setDuration(Duration.ofMinutes(10));
    }

    @AfterEach
    void clearTasks() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicsAndSubTasks();
        taskManager.deleteAllPriorizedTasks();
    }

    /* Tasks */
    @Test
    void createTask() {
        taskFirst = new Task("First task", "Description 1");
        taskManager.createTask(taskFirst);
        assertNotNull(taskManager.getAllTasks(), "Задачи не найдены.");
        int taskId = taskFirst.getId();
        assertEquals(taskFirst, taskManager.getTaskById(taskId), "Задачи не одинаковы.");
    }

    @Test
    void updateTask() {
        taskFirst = new Task("First task", "Description 1");
        taskManager.createTask(taskFirst);
        assertEquals(TaskStatus.NEW, taskFirst.getStatus());
        taskFirst.setStatus(TaskStatus.DONE);
        taskManager.updateTask(taskFirst);
        final Task updatedTask = taskManager.getTaskById(taskFirst.getId());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus(), "Статус задач не совпадает.");
    }

    @Test
    void getAllTasks() {
        taskFirst = new Task("First task", "Description 1");
        taskManager.createTask(taskFirst);
        assertFalse(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void getTaskById() {
        taskSecond = new Task("Second task", "Description 2");
        taskManager.createTask(taskSecond);
        int taskId = taskSecond.getId();
        assertEquals(taskSecond, taskManager.getTaskById(taskId));
    }

    @Test
    void deleteTaskById() {
        Task taskToDelete = new Task("Delete", "Delete me");
        taskManager.createTask(taskToDelete);
        int taskId = taskToDelete.getId();
        taskManager.deleteTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId), "Задача не была удалена.");
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач после удаления не пуст.");
    }

    /* Epics */
    @Test
    void createEpic() {
        // clearTasks();
        taskManager.createEpic(new Epic("new epic", "def"));
        assertNotNull(taskManager.getAllEpics(), "Эпики не найдены.");
        Epic newEpic = taskManager.getAllEpics().get(0);
        int epicId = taskManager.getAllEpics().get(0).getId();
        assertEquals(newEpic, taskManager.getEpicById(epicId), "Эпики не одинаковы.");
    }

    @Test
    void updateEpicCheckSubTasks() {
        taskManager.createSubTask(subTaskFirst);
        taskManager.createSubTask(subTaskSecond);
        List<SubTask> subTasksToCreate = epicFirst.getSubTasks();

        assertNotNull(subTasksToCreate, "Задачей в эпике нет.");

        taskManager.updateEpic(epicFirst);
        List<SubTask> subTasksCreated = taskManager.getEpicSubTasks(epicFirst);
        assertEquals(2, subTasksCreated.size());
        assertEquals(epicFirst.getSubTasks(), taskManager.getEpicSubTasks(epicFirst));
    }

    @Test
    void getAllEpics() {
        assertNotNull(taskManager.getAllEpics(), "Эпиков не найдено.");
    }

    @Test
    void getEpicById() {
        taskManager.createEpic(epicSecond);
        final int epicId = epicSecond.getId();
        assertNotNull(taskManager.getEpicById(epicId), "Эпик не с id " + epicId + " найден.");
    }

    @Test
    void deleteEpicById() {
        final int epicId = epicSecond.getId();
        taskManager.deleteEpicById(epicId);
        assertNull(taskManager.getEpicById(epicId), "Эпик с id " + epicId + " существует.");
    }

    @Test
    void deleteAllEpicsAndSubTasks() {
        taskManager.createEpic(epicSecond);

        epicIdSecond = epicSecond.getId();
        subTaskThird = new SubTask("Subtask 3", "Description", epicIdSecond);
        subTaskFourth = new SubTask("Subtask 4", "Description", epicIdSecond);

        taskManager.createSubTask(subTaskThird);
        taskManager.createSubTask(subTaskFourth);

        final int subtaskIdThird = subTaskThird.getId();
        final int subtaskIdFourth = subTaskFourth.getId();

        epicSecond.setSubTasks(new ArrayList<>(Arrays.asList(subTaskThird, subTaskFourth)));
        taskManager.updateEpic(epicSecond);
        assertNotNull(taskManager.getEpicById(epicIdSecond));

        taskManager.deleteAllEpicsAndSubTasks();
        assertNull(taskManager.getEpicById(epicIdSecond), "Эпик с id " + epicIdSecond + " существует.");
        assertNull(taskManager.getSubTaskById(subtaskIdThird), "Подзадача с id " + subtaskIdThird + " существует.");
        assertNull(taskManager.getEpicById(subtaskIdFourth), "Подзадача с id " + subtaskIdFourth + " существует.");
    }

    @Test
    void epicStatusTest() {
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
    void deleteSubtasksFromEpic() {
        taskManager.createSubTask(subTaskThird);
        taskManager.createSubTask(subTaskFourth);

        final int subtaskIdFifth = subTaskThird.getId();

        epicSecond.setSubTasks(new ArrayList<>(Arrays.asList(subTaskThird, subTaskFourth)));
        taskManager.updateEpic(epicSecond);
        assertEquals(2, taskManager.getEpicById(epicIdSecond).getSubTasks().size());

        taskManager.deleteSubTaskById(subtaskIdFifth);
        assertEquals(1, taskManager.getEpicById(epicIdSecond).getSubTasks().size());

        for (SubTask subTask : taskManager.getEpicSubTasks(epicSecond)) {
            assertNotEquals(subtaskIdFifth, subTask.getId());
        }
    }


    /* SubTask */
    @Test
    void createSubTask() {
        taskManager.createSubTask(subTaskFirst);
        int subTaskId = subTaskFirst.getId();
        assertEquals(subTaskFirst, taskManager.getSubTaskById(subTaskId), "Подзадачи не одинаковы.");
    }

    @Test
    void updateSubTask() {
        if (taskManager.getAllSubTasks().isEmpty()) {
            taskManager.createSubTask(subTaskFirst);
        }
        subTaskFirst.setDescription("lala");
        taskManager.updateSubTask(subTaskFirst);
        assertEquals("lala", subTaskFirst.getDescription(), "Описание подзадачи не совпадает");
    }

}
