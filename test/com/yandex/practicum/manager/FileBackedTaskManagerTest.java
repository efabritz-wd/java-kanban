package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    public static File file = null;
    public static FileBackedTaskManager backupManager;

    @BeforeAll
    public static void beforeAll() throws IOException {
        file = File.createTempFile("test", ".csv");
        backupManager = (FileBackedTaskManager) Managers.getDefaultBackup(file);
        backupManager.resetFile();
    }

    @AfterEach
    public void afterEach() {
        backupManager.deleteAllTasks();
        backupManager.deleteAllEpicsAndSubTasks();
    }

    @Test
    void createTask() throws IOException {
        Task taskFirst = new Task("First task", "Description 1");
        backupManager.createTask(taskFirst);
        String line = backupManager.toString(taskFirst);
        List<String> linesFound = backupManager.readFromFile();
        assertEquals(2, linesFound.size());
        if (linesFound.size() > 1) {
            assertEquals(line, linesFound.get(1));
        }
    }

    @Test
    void createEpic() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");

        backupManager.createEpic(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());

        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        List<String> linesFound = backupManager.readFromFile();
        assertTrue(linesFound.contains(backupManager.toString(epicFirst)));
        assertTrue(linesFound.contains(backupManager.toString(subTaskFirst)));
        assertTrue(linesFound.contains(backupManager.toString(subTaskSecond)));
    }

    @Test
    void updateTask() throws IOException {
        Task taskSecond = new Task("Second task", "Description 2");
        backupManager.createTask(taskSecond);
        String lineOld = backupManager.toString(taskSecond);
        taskSecond.setDescription("NEWDESC");
        backupManager.updateTask(taskSecond);
        String lineNew = backupManager.toString(taskSecond);
        List<String> linesFound = backupManager.readFromFile();

        assertFalse(linesFound.contains(lineOld));
        assertTrue(linesFound.contains(lineNew));
    }

    @Test
    void updateEpic() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");
        backupManager.createEpic(epicFirst);
        String epicOld = backupManager.toString(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());

        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        String subTaskFirstOld = backupManager.toString(subTaskFirst);
        String subTaskSecondOld = backupManager.toString(subTaskSecond);

        epicFirst.setTitle("new epic");
        subTaskFirst.setDescription("dew description");
        subTaskSecond.setTitle("new subtask title");

        backupManager.updateEpic(epicFirst);
        backupManager.updateSubTask(subTaskFirst);
        backupManager.updateSubTask(subTaskSecond);

        String epicNew = backupManager.toString(epicFirst);
        String subTaskFirstNew = backupManager.toString(subTaskFirst);
        String subTaskSecondNew = backupManager.toString(subTaskSecond);

        List<String> linesFound = backupManager.readFromFile();

        assertFalse(linesFound.contains(epicOld));
        assertFalse(linesFound.contains(subTaskFirstOld));
        assertFalse(linesFound.contains(subTaskSecondOld));

        assertTrue(linesFound.contains(epicNew));
        assertTrue(linesFound.contains(subTaskFirstNew));
        assertTrue(linesFound.contains(subTaskSecondNew));
    }

    @Test
    void deleteTaskById() throws IOException {
        Task taskFirst = new Task("First task", "Description 1");
        backupManager.createTask(taskFirst);
        String line = backupManager.toString(taskFirst);
        backupManager.deleteTaskById(taskFirst.getId());
        List<String> linesFound = backupManager.readFromFile();
        assertFalse(linesFound.contains(line));
    }

    @Test
    void deleteAllTasks() throws IOException {
        Task taskFirst = new Task("First task", "Description 1");
        backupManager.createTask(taskFirst);
        Task taskSecond = new Task("First second", "Description 2");
        backupManager.createTask(taskSecond);
        String lineFirst = backupManager.toString(taskFirst);
        String lineSecond = backupManager.toString(taskSecond);
        backupManager.deleteAllTasks();
        List<String> linesFound = backupManager.readFromFile();
        assertFalse(linesFound.contains(lineFirst));
        assertFalse(linesFound.contains(lineSecond));
    }

    @Test
    void deleteAllEpicsAndSubTasks() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");
        Epic epicSecond = new Epic("Second epic", "epic description");

        backupManager.createEpic(epicFirst);
        backupManager.createEpic(epicSecond);

        String epicFirstOld = backupManager.toString(epicFirst);
        String epicSecondOld = backupManager.toString(epicSecond);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());

        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        String subTaskFirstOld = backupManager.toString(subTaskFirst);
        String subTaskSecondOld = backupManager.toString(subTaskSecond);

        backupManager.deleteAllEpicsAndSubTasks();
        List<String> linesFound = backupManager.readFromFile();

        assertFalse(linesFound.contains(subTaskFirstOld));
        assertFalse(linesFound.contains(subTaskSecondOld));
        assertFalse(linesFound.contains(epicFirstOld));
        assertFalse(linesFound.contains(epicSecondOld));
    }

    @Test
    void deleteEpicById() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");

        backupManager.createEpic(epicFirst);

        String epicFirstOld = backupManager.toString(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());

        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        String subTaskFirstOld = backupManager.toString(subTaskFirst);
        String subTaskSecondOld = backupManager.toString(subTaskSecond);

        backupManager.deleteEpicById(epicFirst.getId());
        List<String> linesFound = backupManager.readFromFile();

        assertFalse(linesFound.contains(subTaskFirstOld));
        assertFalse(linesFound.contains(subTaskSecondOld));
        assertFalse(linesFound.contains(epicFirstOld));
    }

    @Test
    void deleteAllEpicSubTasks() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");
        backupManager.createEpic(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());
        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        String subTaskFirstOld = backupManager.toString(subTaskFirst);
        String subTaskSecondOld = backupManager.toString(subTaskSecond);

        backupManager.deleteAllEpicSubTasks(epicFirst);
        List<String> linesFound = backupManager.readFromFile();
        assertFalse(linesFound.contains(subTaskFirstOld));
        assertFalse(linesFound.contains(subTaskSecondOld));
    }

    @Test
    void deleteAllSubTasks() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");
        backupManager.createEpic(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());
        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        String subTaskFirstOld = backupManager.toString(subTaskFirst);
        String subTaskSecondOld = backupManager.toString(subTaskSecond);

        backupManager.deleteAllSubTasks();
        List<String> linesFound = backupManager.readFromFile();
        assertFalse(linesFound.contains(subTaskFirstOld));
        assertFalse(linesFound.contains(subTaskSecondOld));
    }

    @Test
    void loadFromFile() throws IOException {
        List<Task> oldTasks = createTasks();

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size());
    }

    List<Task> createTasks() throws IOException {
        File fileLoad = File.createTempFile("test1", ".csv");
        FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getDefaultBackup(fileLoad);
        manager.resetFile();
        Task taskFirst = new Task("First task", "Description 1");
        manager.createTask(taskFirst);

        Task taskSecond = new Task("Second task", "Description 2");
        manager.createTask(taskSecond);

        List<Task> list = new ArrayList<>();
        list.add(taskFirst);
        list.add(taskSecond);
        return list;
    }


}