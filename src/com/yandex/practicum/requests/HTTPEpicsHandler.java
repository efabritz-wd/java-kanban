package com.yandex.practicum.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.utils.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class HTTPEpicsHandler extends BaseHttpHandler implements HttpHandler {

    public HTTPEpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        try {
            switch (method) {
                case "POST":
                    handleEpicPostRequest(httpExchange);
                    break;
                case "GET":
                    handleEpicGetRequest(httpExchange);
                    break;
                case "DELETE":
                    handleEpicDeleteRequest(httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
            throw new NotFoundException("Task was not found");
        }
    }

    private void handleEpicGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        if (pathArray.length == 2 && pathArray[1].equals("epics")) {
            List<Epic> epics = taskManager.getAllEpics();
            String response = gson.toJson(epics);
            sendText(httpExchange, response, 200);
        } else if ((pathArray.length == 3) && pathArray[1].equals("epics")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            String response = gson.toJson(taskManager.getEpicById(id.get()));
            sendText(httpExchange, response, 200);
        } else if ((pathArray.length == 4) && pathArray[1].equals("epics") && pathArray[3].equals("subtasks")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            if (id.isPresent()) {
                Optional<Epic> epic = Optional.ofNullable(taskManager.getEpicById(id.get()));
                if (epic.isPresent()) {
                    String response = gson.toJson(epic.get().getSubTasks());
                    sendText(httpExchange, response, 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } else {
                sendNotFound(httpExchange);
            }
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }

    private void handleEpicPostRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        if (jsonObject.has("title")
                && jsonObject.has("description")
                && jsonObject.has("type")) {
            if (jsonObject.has("id")) {
                try {
                    Task taskToUpdate = taskManager.getEpicById(jsonObject.get("id").getAsInt());
                    Task taskWithUpdatedParams = setTaskParameters(jsonObject, taskToUpdate, false);
                    try {
                        taskManager.updateEpic((Epic) taskWithUpdatedParams);
                        sendText(httpExchange, "Epic updated", 201);
                    } catch (Exception e) {
                        sendHasInteractions(httpExchange);
                    }
                } catch (Exception e) {
                    sendText(httpExchange, "Error during epic update", 400);
                }
            } else {
                try {
                    Epic epic = (Epic) createTaskWithParameters(jsonObject);
                    taskManager.createEpic(epic);
                    sendText(httpExchange, "Epic created", 201);
                } catch (Exception e) {
                    sendHasInteractions(httpExchange);
                }
            }
        } else {
            sendText(httpExchange, "Incorrect params for epic creation", 400);
        }
    }

    private void handleEpicDeleteRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");

        if ((pathArray.length == 3) && pathArray[1].equals("epics")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            taskManager.deleteEpicById(id.get());
            sendText(httpExchange, "Epic with id: " + id.get() + " was deleted.", 200);
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }

}
