package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public static Task taskFirst;
    public static Task taskSecond;
    public static Epic epicFirst;
    public static Epic epicSecond;
    public static SubTask subTaskFirst;
    public static SubTask subTaskSecond;
    public static SubTask subTaskThird;
    public static SubTask subTaskFourth;
    public static TaskManager taskManager;
    public static int epicIdSecond;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();

        epicFirst = new Epic("First epic");
        epicSecond = new Epic("Second epic");

        taskManager.createEpic(epicFirst);
        taskManager.createEpic(epicSecond);

        subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());

        epicIdSecond = epicSecond.getId();
        subTaskThird = new SubTask("Subtask 3", "Description", epicIdSecond);
        subTaskFourth = new SubTask("Subtask 4", "Description", epicIdSecond);
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
        assertNotNull(taskManager.getAllEpics(), "Эпики не найдены.");
        int epicId = epicFirst.getId();
        assertEquals(epicFirst, taskManager.getEpicById(epicId), "Эпики не одинаковы.");
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


    /* SubTask */
    @Test
    void createSubTask() {
        taskManager.createSubTask(subTaskFirst);
        int subTaskId = subTaskFirst.getId();
        assertEquals(subTaskFirst, taskManager.getSubTaskById(subTaskId), "Подзадачи не одинаковы.");
    }

    @Test
    void updateSubTask() {
        if(taskManager.getAllSubTasks().isEmpty()) {
            taskManager.createSubTask(subTaskFirst);
        }
        subTaskFirst.setDescription("lala");
        taskManager.updateSubTask(subTaskFirst);
        assertEquals("lala", subTaskFirst.getDescription(), "Описание подзадачи не совпадает");
    }

    @Test
    void getAndDeleteSubTaskById() {
        if(taskManager.getAllSubTasks().isEmpty()) {
            taskManager.createSubTask(subTaskFirst);
        }
        final SubTask subTask = taskManager.getAllSubTasks().get(0);
        final int subTaskId = subTask.getId();
        assertNotNull(taskManager.getSubTaskById(subTaskId), "Подзадача не найдена.");

        taskManager.deleteSubTaskById(subTaskId);
        assertNull(taskManager.getSubTaskById(subTaskId), "Подзадача найдена.");

    }

    /* other tests */

    @Test
    void createTaskIdConflict() {
        Task task1;
        Integer taks1Id;
        if(!taskManager.getAllTasks().isEmpty()) {
            task1 = taskManager.getAllTasks().get(0);
            taks1Id = task1.getId();
        } else {
            task1 = new Task("First task 1", "Description 1");
            task1.setId(0);
            taks1Id = 0;
        }

        Task task2 = new Task("First task 2", "Description 2");
        task2.setId(taks1Id);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID одинаковы.");
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

}