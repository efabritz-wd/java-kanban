package com.yandex.practicum.requests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.utils.NotFoundException;

import java.io.IOException;

public class HTTPPrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public HTTPPrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handlePrioritizedGetRequest(httpExchange);
                default:
                    sendText(httpExchange, "Method is not supported", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
            throw new NotFoundException("Task was not found");
        }
    }

    private void handlePrioritizedGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        if (pathArray.length == 2 && pathArray[1].equals("prioritized")) {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(httpExchange, response, 200);
        } else {
            sendText(httpExchange, "Incorrect path", 400);
        }
    }
}
