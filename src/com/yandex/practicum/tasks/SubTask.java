package com.yandex.practicum.tasks;

public class SubTask extends Task {
    private int epic;

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    public SubTask(int id, String title, int epic) {
        super(id, title);
        this.epic = epic;
    }

    public SubTask(int id, String title, String description, int epic) {
        super(id, title, description);
        this.epic = epic;
    }
}
