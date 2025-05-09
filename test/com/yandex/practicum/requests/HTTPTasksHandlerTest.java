package com.yandex.practicum.requests;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.yandex.practicum.manager.FileBackedTaskManager;
import com.yandex.practicum.manager.Managers;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.tasks.TaskStatus;
import com.yandex.practicum.tasks.TaskType;
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

class HTTPTasksHandlerTest {

    HttpServer httpServer;
    File file = File.createTempFile("test", ".csv");
    TaskManager manager = Managers.getDefaultBackup(file);
    HTTPTaskServer taskServer = new HTTPTaskServer(file, manager);

    Gson gson = new BaseHttpHandler().getGson();

    HTTPTasksHandlerTest() throws IOException {
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
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setEndTime(LocalDateTime.now());
        task.setType(TaskType.TASK);
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setEndTime(LocalDateTime.now());
        task.setType(TaskType.TASK);
        manager.createTask(task);

        task.setDescription("New description");

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New description", tasksFromManager.get(0).getDescription(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        task.setType(TaskType.TASK);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task taskFirst = new Task("Test 1", "Testing task 1");
        taskFirst.setStatus(TaskStatus.NEW);
        taskFirst.setDuration(Duration.ofMinutes(15));
        taskFirst.setStartTime(LocalDateTime.now().plusHours(1));
        taskFirst.setType(TaskType.TASK);

        Task taskSecond = new Task("Test 1", "Testing task 1");
        manager.createTask(taskFirst);
        manager.createTask(taskSecond);

        HttpClient client = HttpClient.newHttpClient();
        URI urlFirst = URI.create("http://localhost:8080/tasks/");
        URI urlSecond = URI.create("http://localhost:8080/tasks/" + taskFirst.getId());
        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(urlFirst).GET().build();
        HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlSecond).GET().build();

        HttpResponse<String> responseAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseAll.statusCode());
        assertEquals(200, responseGetById.statusCode());

        assertEquals(gson.toJson(manager.getAllTasks()), responseAll.body(), "Найденые задачи не совпадают");
        assertEquals(gson.toJson(manager.getTaskById(taskFirst.getId())), responseGetById.body(), "Найденая задача не совпадает");
    }
}