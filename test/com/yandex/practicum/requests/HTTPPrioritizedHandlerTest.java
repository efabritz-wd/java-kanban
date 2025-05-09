package com.yandex.practicum.requests;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.yandex.practicum.manager.*;
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

class HTTPPrioritizedHandlerTest {

    HttpServer httpServer;
    File file = File.createTempFile("test", ".csv");
    TaskManager manager = Managers.getDefaultBackup(file);
    HTTPTaskServer taskServer = new HTTPTaskServer(file, manager);

    Gson gson = new BaseHttpHandler().getGson();

    HTTPPrioritizedHandlerTest() throws IOException {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task taskFirst = new Task("Test 1", "Testing task 1");
        taskFirst.setStatus(TaskStatus.NEW);
        taskFirst.setDuration(Duration.ofMinutes(5));
        taskFirst.setStartTime(LocalDateTime.now().plusHours(1));
        taskFirst.setType(TaskType.TASK);

        Task taskSecond = new Task("Test 2", "Testing task 2");
        taskFirst.setStatus(TaskStatus.NEW);
        taskFirst.setDuration(Duration.ofMinutes(15));
        taskFirst.setStartTime(LocalDateTime.now().plusHours(2));
        taskFirst.setType(TaskType.TASK);
        manager.createTask(taskFirst);
        manager.createTask(taskSecond);

        manager.getAllTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI urlFirst = URI.create("http://localhost:8080/prioritized/");

        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(urlFirst).GET().build();
        HttpResponse<String> responseAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAll.statusCode());
        assertEquals(gson.toJson(manager.getPrioritizedTasks()), responseAll.body(), "Найденые задачи не совпадают");
    }
}