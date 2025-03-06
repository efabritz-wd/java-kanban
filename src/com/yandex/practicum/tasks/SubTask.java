package com.yandex.practicum.tasks;

public class SubTask extends Task {
    private int epic;

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        if(this.getId() != null && epic == this.getId()) {
            System.out.println("Эпик id не может совпадать с id создаваемой подзадачи");
            this.epic = -1;
            return;
        }
        this.epic = epic;
    }

    public SubTask(String title, int epic) {
        super(title);
        setEpic(epic);
    }

    public SubTask(String title, String description, int epic) {
        super(title, description);
        setEpic(epic);
    }
}
