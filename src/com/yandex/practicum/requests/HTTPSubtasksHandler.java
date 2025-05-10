package com.yandex.practicum.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Epic;
import com.yandex.practicum.tasks.SubTask;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.utils.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HTTPSubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public HTTPSubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        try {
            switch (method) {
                case "POST":
                    handleSubtaskPostRequest(httpExchange);
                    break;
                case "GET":
                    handleSubtaskGetRequest(httpExchange);
                    break;
                case "DELETE":
                    handleSubtaskDeleteRequest(httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
            throw new NotFoundException("Task was not found");
        }
    }

    private void handleSubtaskGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        if (pathArray.length == 2 && pathArray[1].equals("subtasks")) {
            String response = gson.toJson(taskManager.getAllSubTasks());
            sendText(httpExchange, response, 200);
        } else if ((pathArray.length == 3) && pathArray[1].equals("subtasks")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            String response = gson.toJson(taskManager.getSubTaskById(id.get()));
            sendText(httpExchange, response, 200);
        } else {
            sendNotFound(httpExchange);
        }
    }

    private void handleSubtaskPostRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        if (jsonObject.has("title")
                && jsonObject.has("description")
                && jsonObject.has("type")
                && jsonObject.has("epic")) {
            if (jsonObject.has("id")) {
                try {
                    Task taskToUpdate = taskManager.getSubTaskById(jsonObject.get("id").getAsInt());
                    Task taskWithUpdatedParams = setTaskParameters(jsonObject, taskToUpdate, false);
                    try {
                        taskManager.updateTask(taskWithUpdatedParams);
                        sendText(httpExchange, "subtask updated", 201);
                    } catch (IOException e) {
                        sendHasInteractions(httpExchange);
                    }
                } catch (Exception e) {
                    sendText(httpExchange, "Error during subtask update", 400);
                }
            } else {
                try {
                    Integer epicId = jsonObject.get("epic").getAsInt();
                    Optional<Epic> epicFound = taskManager.getAllEpics().stream()
                            .filter(epic -> epic.getId() == epicId).findFirst();
                    if (epicFound.isPresent()) {
                        SubTask subTask = (SubTask) createTaskWithParameters(jsonObject);
                        taskManager.createSubTask(subTask);
                        sendText(httpExchange, "Task created", 201);
                    } else {
                        sendText(httpExchange, "Sutasks epic id " + epicId + " was not found", 404);
                    }
                } catch (Exception e) {
                    sendHasInteractions(httpExchange);
                }
            }
        } else {
            sendText(httpExchange, "Icorrect params for task creation", 400);
        }
    }

    private void handleSubtaskDeleteRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");

        if ((pathArray.length == 3) && pathArray[1].equals("subtasks")) {
            Optional<Integer> id = Optional.of(Integer.parseInt(pathArray[2]));
            taskManager.deleteSubTaskById(id.get());
            sendText(httpExchange, "SubTask with id: " + id.get() + " was deleted.", 200);
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }
}