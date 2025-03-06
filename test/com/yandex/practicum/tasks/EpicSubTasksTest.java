package com.yandex.practicum.tasks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicSubTasksTest {

    public static Epic epic;
    public static SubTask subTask;
    public static ArrayList<SubTask> subTasks = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {
        epic = new Epic("Epic title");
        epic.setId(0);
        subTask = new SubTask("First task", "Description 1", epic.getId());
        subTask.setId(1);
        subTasks.add(subTask);
        epic.setSubTasks(subTasks);
    }

    @Test
    void getSubTasks() {
        assertNotNull(epic.getSubTasks());
        assertEquals(subTasks.size(), epic.getSubTasks().size());

        for(int i=0; i < epic.getSubTasks().size(); i++) {
           SubTask subTaskEpic = epic.getSubTasks().get(i);
           assertEquals(subTask, subTaskEpic);
        }
    }

    @Test
    void getEpic() {
        if(!epic.getSubTasks().isEmpty() && epic.getSubTasks().get(0) != null) {
            assertEquals(epic.getId(), epic.getSubTasks().get(0).getEpic());
        }
    }

    @Test
    void addEpicAsSubtaskOfItself() {
        Epic epic = new Epic("epic");
        epic.setId(10);
        SubTask subTask = new SubTask("New subtask",  "Description", epic.getId());
        subTask.setId(10);
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.add(subTask);
        epic.setSubTasks(subTasks);
        assertTrue(epic.getSubTasks().isEmpty());

    }

    @Test
    void addSubtaskIsOwnEpic() {
        Epic epic = new Epic("epic");
        epic.setId(10);
        SubTask subTask = new SubTask("New subtask", "Description", 10);
        subTask.setId(10);
        subTask.setEpic(subTask.getId());
        assertEquals(-1, subTask.getEpic());
    }
}