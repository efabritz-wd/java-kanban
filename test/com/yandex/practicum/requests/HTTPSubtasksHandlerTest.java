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

class HTTPSubtasksHandlerTest {

    HttpServer httpServer;
    File file = File.createTempFile("test", ".csv");
    TaskManager manager = Managers.getDefaultBackup(file);
    HTTPTaskServer taskServer = new HTTPTaskServer(file, manager);

    Gson gson = new BaseHttpHandler().getGson();

    HTTPSubtasksHandlerTest() throws IOException {
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
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setEndTime(LocalDateTime.now());
        epic.setType(TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("subtask 1", "subtask description", epic.getId());
        subTask.setDuration(Duration.ofMinutes(10));
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        manager.createSubTask(subTask);

        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask 1", tasksFromManager.get(0).getTitle(), "Некорректное описание задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        epic.setStatus(TaskStatus.NEW);
        epic.setDuration(Duration.ofMinutes(10));
        epic.setEndTime(LocalDateTime.now());
        epic.setType(TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("subtask 1", "subtask description", epic.getId());
        subTask.setDuration(Duration.ofMinutes(10));
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        manager.createSubTask(subTask);

        subTask.setDescription("New description");

        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New description", tasksFromManager.get(0).getDescription(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSutask() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Testing epic 1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setEndTime(LocalDateTime.now());
        task.setType(TaskType.EPIC);
        manager.createEpic(task);

        SubTask subTask = new SubTask("subtask 1", "subtask description", task.getId());
        subTask.setDuration(Duration.ofMinutes(10));
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        manager.createSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 1", "Testing epic 1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        task.setType(TaskType.EPIC);
        manager.createEpic(task);

        SubTask subTask = new SubTask("subtask 1", "subtask description", task.getId());
        subTask.setDuration(Duration.ofMinutes(10));
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        manager.createSubTask(subTask);

        SubTask subTaskNext = new SubTask("subtask 2", "subtask 2 description", task.getId());
        subTaskNext.setDuration(Duration.ofMinutes(20));
        subTaskNext.setStartTime(LocalDateTime.now().plusHours(3));
        manager.createSubTask(subTaskNext);

        HttpClient client = HttpClient.newHttpClient();
        URI urlFirst = URI.create("http://localhost:8080/subtasks/");
        URI urlSecond = URI.create("http://localhost:8080/subtasks/" + subTask.getId());

        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(urlFirst).GET().build();
        HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlSecond).GET().build();

        HttpResponse<String> responseAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseAll.statusCode());
        assertEquals(200, responseGetById.statusCode());

        assertEquals(gson.toJson(manager.getAllSubTasks()), responseAll.body(), "Найденые задачи не совпадают");
        assertEquals(gson.toJson(manager.getSubTaskById(subTask.getId())), responseGetById.body(), "Найденая задача не совпадает");
    }
}