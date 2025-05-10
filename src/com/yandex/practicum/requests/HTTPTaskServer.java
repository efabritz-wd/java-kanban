package com.yandex.practicum.requests;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import com.yandex.practicum.manager.Managers;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;


public class HTTPTaskServer {
    private static final int PORT = 8080;
    public static File file = new File("./data/tasks.csv");
    public static TaskManager taskManager = Managers.getDefaultBackup(file);
    public static HttpServer httpServer;

    public HTTPTaskServer(File file, TaskManager taskManager) throws IOException {
        this.file = file;
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HTTPTasksHandler(taskManager));
        httpServer.createContext("/subtasks", new HTTPSubtasksHandler(taskManager));
        httpServer.createContext("/epics", new HTTPEpicsHandler(taskManager));
        httpServer.createContext("/history", new HTTPHistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new HTTPPrioritizedHandler(taskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер запущен остановлен!");
    }

    public static class TaskListTypeToken extends TypeToken<List<? extends Task>> {
    }
}
