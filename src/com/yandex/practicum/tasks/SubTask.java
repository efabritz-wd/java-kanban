package com.yandex.practicum.tasks;

public class SubTask extends Task {
    private int epic;

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    public SubTask(String title, int epic) {
        super(title);
        this.epic = epic;
    }

    public SubTask(String title, String description, int epic) {
        super(title, description);
        this.epic = epic;
    }
}
