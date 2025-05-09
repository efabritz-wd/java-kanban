package com.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd:MM:yyyy");
    private Integer id;
    private String title;
    private String description = "";
    private TaskStatus status = TaskStatus.NEW;
    private TaskType type = TaskType.TASK;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String title) {
        this.title = title;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ZERO;
        this.endTime = LocalDateTime.now();
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (this.type.equals(TaskType.EPIC)) {
            return this.endTime;
        }
        try {
            this.endTime = this.startTime.plus(this.duration);
            return this.endTime;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setEndTime(LocalDateTime dateTime) {
        this.endTime = dateTime;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + "', status=" + status +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

