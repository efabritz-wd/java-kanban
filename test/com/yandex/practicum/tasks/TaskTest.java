package com.yandex.practicum.tasks;

import com.yandex.practicum.utils.AccessControl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends AccessControl {

    public static Task taskFirst;
    public static Task taskSecond;

    @BeforeAll
    public static void beforeAll() {
        AccessControl.allow();
        taskFirst = new Task("First task", "Description 1");
        taskSecond = new Task("Second task", "Description 2");
    }

    @Test
    void getId() {
        assertNotNull(taskFirst, "First task created");
        taskFirst.setId(0);
        assertEquals(0, taskFirst.getId());
    }

    @Test
    void getTitle() {
        assertEquals("First task", taskFirst.getTitle());
    }

    @Test
    void getDescription() {
        assertEquals("Description 2", taskSecond.getDescription());
    }

    @Test
    void getStatus() {
        assertEquals(TaskStatus.NEW, taskFirst.getStatus());
    }
}