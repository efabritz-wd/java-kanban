package com.yandex.practicum.requests;

import static org.junit.jupiter.api.Assertions.*;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.yandex.practicum.manager.FileBackedTaskManager;
import com.yandex.practicum.manager.Managers;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HTTPEpicsHandlerTest {

    HttpServer httpServer;
    File file = File.createTempFile("test", ".csv");
    TaskManager manager = Managers.getDefaultBackup(file);
    HTTPTaskServer taskServer = new HTTPTaskServer(file, manager);

    Gson gson = new BaseHttpHandler().getGson();

    HTTPEpicsHandlerTest() throws IOException {
    }


    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpicsAndSubTasks();
        ((FileBackedTaskManager) manager).resetFile();
        taskServer.start();
        httpServer = HTTPTaskServer.httpServer;
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setEndTime(LocalDateTime.now());
        epic.setType(TaskType.EPIC);
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        
        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Testing epic 1", tasksFromManager.get(0).getDescription(), "Некорректное описание задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "New description");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setEndTime(LocalDateTime.now());
        epic.setType(TaskType.EPIC);

        manager.createEpic(epic);

        epic.setDescription("New description");

        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New description", tasksFromManager.get(0).getDescription(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Testing epic 1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        task.setType(TaskType.TASK);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Epic taskFirst = new Epic("Epic 1", "Testing epic 1");
        taskFirst.setStatus(TaskStatus.NEW);
        taskFirst.setDuration(Duration.ofMinutes(5));
        taskFirst.setEndTime(LocalDateTime.now());
        taskFirst.setType(TaskType.TASK);

        Epic taskSecond = new Epic("Epic 2", "Testing epic 1");
        manager.createEpic(taskFirst);
        manager.createEpic(taskSecond);
        SubTask subTask = new SubTask("subtask 1", "subtask description", taskFirst.getId());
        subTask.setDuration(Duration.ofMinutes(10));
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        manager.createSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI urlFirst = URI.create("http://localhost:8080/epics/");
        URI urlSecond = URI.create("http://localhost:8080/epics/" + taskFirst.getId());
        URI urlThird = URI.create("http://localhost:8080/epics/" + taskFirst.getId() + "/subtasks");
        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(urlFirst).GET().build();
        HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlSecond).GET().build();
        HttpRequest requestEpicSubtasks = HttpRequest.newBuilder().uri(urlThird).GET().build();

        HttpResponse<String> responseAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEpicSubtasks = client.send(requestEpicSubtasks, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseAll.statusCode());
        assertEquals(200, responseGetById.statusCode());
        assertEquals(200, responseEpicSubtasks.statusCode());

        assertEquals(gson.toJson(manager.getAllEpics()), responseAll.body(), "Найденые задачи не совпадают");
        assertEquals(gson.toJson(manager.getEpicById(taskFirst.getId())), responseGetById.body(), "Найденая задача не совпадает");
        assertEquals(gson.toJson(manager.getEpicById(taskFirst.getId()).getSubTasks()), responseEpicSubtasks.body(), "Подзадачи не совпадают");
    }
}