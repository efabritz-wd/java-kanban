package com.yandex.practicum.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Task;

import com.yandex.practicum.utils.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


public class HTTPTasksHandler extends BaseHttpHandler implements HttpHandler {

    public HTTPTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        try {

            switch (method) {
                case "POST":
                    handleTaskPostRequest(httpExchange);
                    break;
                case "GET":
                    handleTaskGetRequest(httpExchange);
                    break;
                case "DELETE":
                    handleTaskDeleteRequest(httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
            throw new NotFoundException("Task was not found");
        }
    }

    private void handleTaskGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        if (pathArray.length == 2 && pathArray[1].equals("tasks")) {
            List<Task> tasks = taskManager.getAllTasks();
            String response = gson.toJson(tasks);
            sendText(httpExchange, response, 200);
        } else if ((pathArray.length == 3) && pathArray[1].equals("tasks")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            String response = gson.toJson(taskManager.getTaskById(id.get()));
            sendText(httpExchange, response, 200);
        } else {
            sendNotFound(httpExchange);
        }
    }

    private void handleTaskPostRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        if (jsonObject.has("title")
                && jsonObject.has("description")
                && jsonObject.has("type")) {
            if (jsonObject.has("id")) {
                try {
                    Task taskToUpdate = taskManager.getTaskById(jsonObject.get("id").getAsInt());
                    Task taskWithUpdatedParams = setTaskParameters(jsonObject, taskToUpdate, false);
                    try {
                        taskManager.updateTask(taskWithUpdatedParams);
                        sendText(httpExchange, "Task updated", 201);
                    } catch (Exception e) {
                        sendHasInteractions(httpExchange);
                    }
                } catch (Exception e) {
                    sendText(httpExchange, "Error during task update", 400);
                }
            } else {
                try {
                    Task task = createTaskWithParameters(jsonObject);
                    taskManager.createTask(task);
                    sendText(httpExchange, "Task created", 201);
                } catch (Exception e) {
                    sendHasInteractions(httpExchange);
                }
            }
        } else {
            sendText(httpExchange, "Icorrect params for task creation", 400);
        }
    }

    private void handleTaskDeleteRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");

        if ((pathArray.length == 3) && pathArray[1].equals("tasks")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            taskManager.deleteTaskById(id.get());
            sendText(httpExchange, "Task with id: " + id.get() + " was deleted.", 200);
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }
}