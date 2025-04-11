package com.yandex.practicum.tasks;

public class SubTask extends Task {
    private int epic;

    public SubTask(String title, int epic) {
        super(title);
        setEpic(epic);
        this.setType(TaskType.SUBTASK);
    }

    public SubTask(String title, String description, int epic) {
        super(title, description);
        setEpic(epic);
        this.setType(TaskType.SUBTASK);
    }

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        if (this.getId() != null && epic == this.getId()) {
            this.epic = -1;
            return;
        }
        this.epic = epic;
    }
}
