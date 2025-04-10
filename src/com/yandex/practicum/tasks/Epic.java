package com.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<SubTask> subTasks = new ArrayList<>();

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
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == this.getId()) {
                return;
            }
        }
        this.subTasks = subTasks;
    }
}
