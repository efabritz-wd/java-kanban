package com.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<SubTask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title) {
        super(title);
        this.setType(TaskType.EPIC);
    }

    public Epic(String title, String description) {
        super(title, description);
        this.setType(TaskType.EPIC);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        LocalDateTime minDateTime = null;
        LocalDateTime maxDateTime = null;
        Duration durationSum = Duration.ZERO;
        if (!subTasks.isEmpty()) {
            minDateTime = subTasks.getFirst().getStartTime();
            maxDateTime = subTasks.getFirst().getEndTime();
        }
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == this.getId()) {
                return;
            }
            if (subTask.getStartTime().isBefore(minDateTime)) {
                minDateTime = subTask.getStartTime();
            }

            if (subTask.getEndTime().isAfter(maxDateTime)) {
                maxDateTime = subTask.getEndTime();
            }
            durationSum = durationSum.plus(subTask.getDuration());
        }
        this.subTasks = subTasks;
        this.setDuration(durationSum);
        this.setStartTime(minDateTime);
        this.endTime = maxDateTime;
        this.setEndTime(this.endTime);
    }

    public LocalDateTime getEndTime() {
        if (this.endTime == null) {
            this.endTime = this.getStartTime().plus(this.getDuration());
            return this.endTime;
        }
        return this.endTime;
    }
}
