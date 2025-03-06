package com.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String title) {
        super(title);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        for(SubTask subTask : subTasks) {
            if (subTask.getId() == this.getId()) {
                System.out.println("Id подзадачи не может быть равен id эпика");
                return;
            }
        }
        this.subTasks = subTasks;
    }
}
