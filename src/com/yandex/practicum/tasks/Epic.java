package com.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(int id, String title) {
        super(id, title);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
