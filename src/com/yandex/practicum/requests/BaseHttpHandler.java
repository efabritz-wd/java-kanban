package com.yandex.practicum.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.practicum.manager.TaskManager;
import com.yandex.practicum.tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BaseHttpHandler {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd:MM:yyyy");
    public TaskManager taskManager;
    protected Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .setPrettyPrinting()
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Gson getGson() {
        return gson;
    }

    protected void sendText(HttpExchange h, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders();
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] resp = "Tasks interact".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders();
        h.sendResponseHeaders(406, 0);
        h.getResponseBody().write(resp);
        h.close();
    }

    private TaskStatus getTaskStatus(String statusString) {
        switch (statusString.toLowerCase()) {
            case "new":
                return TaskStatus.NEW;
            case "in_progress":
                return TaskStatus.IN_PROGRESS;
            case "done":
                return TaskStatus.DONE;
            default:
                return TaskStatus.NEW;
        }
    }

    private TaskType getTaskType(String typeString) {
        switch (typeString.toLowerCase()) {
            case "task":
                return TaskType.TASK;
            case "epic":
                return TaskType.EPIC;
            case "subtask":
                return TaskType.SUBTASK;
            default:
                return TaskType.TASK;
        }
    }

    protected Task createTaskWithParameters(JsonObject jsonObject) {
        Task task = new Task(jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString());
        if (jsonObject.has("type")) {
            if (getTaskType(jsonObject.get("type").getAsString()).equals(TaskType.SUBTASK)) {
                int epicId = jsonObject.get("epic").getAsInt();
                task = new SubTask(jsonObject.get("title").getAsString(),
                        jsonObject.get("description").getAsString(),
                        epicId);
            } else if (getTaskType(jsonObject.get("type").getAsString()).equals(TaskType.EPIC)) {
                task = new Epic(jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString());
            } else {
                task = new Task(jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString());
            }
        }
        return setTaskParameters(jsonObject, task, true);
    }

    protected Task setTaskParameters(JsonObject jsonObject, Task task, boolean creation) {
        if (jsonObject.has("title") && !creation) {
            task.setTitle(jsonObject.get("title").getAsString());
        }
        if (jsonObject.has("description") && !creation) {
            task.setDescription(jsonObject.get("description").getAsString());
        }
        if (jsonObject.has("status")) {
            task.setStatus(getTaskStatus(jsonObject.get("status").getAsString()));
        }
        if (jsonObject.has("type")) {
            task.setType(getTaskType(jsonObject.get("type").getAsString()));
        }
        if (jsonObject.has("duration")) {
            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
            task.setDuration(duration);
        }
        if (jsonObject.has("start")) {
            LocalDateTime dateTime = LocalDateTime.parse(jsonObject.get("start").getAsString(), dtf);
            task.setStartTime(dateTime);
        }
        if (jsonObject.has("end")) {
            LocalDateTime dateTime = LocalDateTime.parse(jsonObject.get("end").getAsString(), dtf);
            task.setEndTime(dateTime);
        }
        if (jsonObject.has("epic") && !creation) {
            ((SubTask) task).setEpic(jsonObject.get("epic").getAsInt());
        }
        return task;
    }

    public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            jsonWriter.value(localDate.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    public class DurationTypeAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(JsonWriter out, Duration duration) throws IOException {
            out.value(duration.toMinutes());
        }

        @Override
        public Duration read(JsonReader in) throws IOException {
            long seconds = in.nextLong();
            return Duration.ofMinutes(seconds);
        }
    }
}

