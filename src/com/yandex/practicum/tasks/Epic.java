package com.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<SubTask> subTasks = new ArrayList<>();

    public Epic(String title) {
        super(title);
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
