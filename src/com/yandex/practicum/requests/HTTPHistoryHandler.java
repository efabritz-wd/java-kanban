package com.yandex.practicum.requests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.practicum.manager.InMemoryTaskManager;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.Task;
import com.yandex.practicum.utils.NotFoundException;

import java.io.IOException;
import java.util.List;


public class HTTPHistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HTTPHistoryHandler(TaskManager taskManager) throws IOException {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleHistoryGetRequest(httpExchange);
                default:
                    sendText(httpExchange, "Method is not supported", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
            throw new NotFoundException("Task was not found");
        }
    }

    private void handleHistoryGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        if (pathArray.length == 2 && pathArray[1].equals("history")) {
            List<Task> history = ((InMemoryTaskManager) taskManager).historyManager.getHistory();
            String response = gson.toJson(history);
            sendText(httpExchange, response, 200);
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }
}
