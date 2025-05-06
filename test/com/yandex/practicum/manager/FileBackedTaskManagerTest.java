package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.utils.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    public static FileBackedTaskManager backupManager;
    public File file;

    @BeforeEach
    public void beforeAll() throws IOException {
        file = File.createTempFile("test", ".csv");
        backupManager = (FileBackedTaskManager) Managers.getDefaultBackup(file);
        super.setupManager(backupManager);
    }

    @AfterEach
    void clearTasks() {
        backupManager.deleteAllTasks();
        backupManager.deleteAllEpicsAndSubTasks();
        backupManager.deleteAllPriorizedTasks();
    }


    @Test
    void createTaskIO() throws IOException {
        Task taskFirst = new Task("First task", "Description 1");

        taskFirst.setStartTime(LocalDateTime.now());
        taskFirst.setDuration(Duration.ofMinutes(20));
        LocalDateTime endTime = taskFirst.getEndTime();
        LocalDateTime chekStartTime = endTime.minus(taskFirst.getDuration());
        assertTrue(chekStartTime.equals(taskFirst.getStartTime()));

        backupManager.createTask(taskFirst);
        String line = backupManager.toString(taskFirst);
        List<String> linesFound = backupManager.readFromFile();
        if (linesFound.size() > 1) {
            assertTrue(linesFound.contains(line));
        }
    }

    @Test
    void createEpicIO() throws IOException {
        Epic epicFirst = new Epic("First epic", "epic description");

        backupManager.createEpic(epicFirst);

        SubTask subTaskFirst = new SubTask("Subtask 1", "Description", epicFirst.getId());
        SubTask subTaskSecond = new SubTask("Subtask 2", "Description", epicFirst.getId());
        subTaskFirst.setStartTime(LocalDateTime.now());
        subTaskFirst.setDuration(Duration.ofMinutes(20));
        subTaskFirst.getEndTime();

        subTaskSecond.setStartTime(LocalDateTime.now().plusMinutes(30));
        subTaskSecond.setDuration(Duration.ofMinutes(10));
        subTaskSecond.getEndTime();

        backupManager.createSubTask(subTaskFirst);
        backupManager.createSubTask(subTaskSecond);

        assertEquals(epicFirst.getStartTime(), subTaskFirst.getStartTime(), "Время начало эпика неверно");
        assertEquals(epicFirst.getEndTime(), subTaskSecond.getEndTime(), "Время конца эпика неверно");
        assertEquals(epicFirst.getDuration(), subTaskFirst.getDuration().plus(subTaskSecond.getDuration()),
                "Продолжительность эпика не совпадает с продолжительностью подзадач");

        List<String> linesFound = backupManager.readFromFile();
        assertTrue(linesFound.contains(backupManager.toString(epicFirst)));
        assertTrue(linesFound.contains(backupManager.toString(subTaskFirst)));
        assertTrue(linesFound.contains(backupManager.toString(subTaskSecond)));
    }

    @Test
    void updateTaskIO() throws IOException {
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
    void updateEpicIO() throws IOException {
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
    void deleteTaskByIdIO() throws IOException {
        Task taskFirst = new Task("First task", "Description 1");
        backupManager.createTask(taskFirst);
        String line = backupManager.toString(taskFirst);
        backupManager.deleteTaskById(taskFirst.getId());
        List<String> linesFound = backupManager.readFromFile();
        assertFalse(linesFound.contains(line));
    }

    @Test
    void deleteAllTasksIO() throws IOException {
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
    void deleteAllEpicsAndSubTasksIO() throws IOException {
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
    void deleteEpicByIdIO() throws IOException {
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
    void deleteAllEpicSubTasksIO() throws IOException {
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
    void deleteAllSubTasksIO() throws IOException {
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
    void loadFromFile() {
        Task taskFirst = new Task("First task", "Description 1");
        backupManager.createTask(taskFirst);
        Task taskSecond = new Task("Second task", "Description 2");
        backupManager.createTask(taskSecond);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(this.file);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());

        int maxId = 0;
        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
            assertTrue(tasks.contains(task));
        }

        Epic epic = new Epic("epic1", "epic1 description");
        manager.createEpic(epic);
        assertEquals(++maxId, epic.getId());
    }

    @Test
    void loadFromNotExistentFile() {
        File file = new File("nofile.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Загрузка история из несуществующего файла");
    }

    @Test
    void loadingFileSuccessfully() throws IOException {
        assertDoesNotThrow(() -> {
            backupManager.resetFile();
        }, "Загрузка история из существующего файла");
    }
}