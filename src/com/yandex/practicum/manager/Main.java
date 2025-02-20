package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task taskFirst = taskManager.createTask("сортировка мусора", "сортировать мусор по категориям");
        Task taskSecond = taskManager.createTask("собрать документы", "найти и собрать документы");
        System.out.println(taskManager.getAllTasks());
        Epic epic = taskManager.createEpic("Поездка домой");

        SubTask subtaskFirst = taskManager.createSubTask("Билеты", "Заказать билеты", epic.getId());
        SubTask subTaskSecond = taskManager.createSubTask("Документы", "Проверить документы", epic.getId());

        Epic epicSecond = taskManager.createEpic("Кружки детей");
        taskManager.createSubTask("Купить инструмент", "    Выбрать и заказать инструмент", epicSecond.getId());

        Epic epicThird = taskManager.createEpic("Тест");
        SubTask subtaskToDelete = taskManager.createSubTask("Пеовый тест", "Провести первый тест", epicThird.getId());
        taskManager.createSubTask("Второй тест", "Провести второй тест", epicThird.getId());

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());

        for(Epic epicVal : taskManager.epicMap.values()) {
            System.out.println("Эпик: " + epicVal);
            System.out.println("Подзадачи: " + taskManager.getEpicSubTasks(epicVal));
        }

        taskFirst.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskFirst, taskFirst.getId());
        System.out.println("Статус задачи изменен:" + taskManager.taskMap.get(taskFirst.getId()).getStatus());

        subtaskFirst.setStatus(TaskStatus.DONE);
        subTaskSecond.setStatus(TaskStatus.DONE);
        System.out.println("Статус эпика до update: " + epic.getStatus());
        taskManager.updateSubTask(subtaskFirst, subtaskFirst.getId());
        taskManager.updateSubTask(subTaskSecond, subTaskSecond.getId());
        System.out.println("Статус подзадачи изменен:" + taskManager.subtaskMap.get(subtaskFirst.getId()).getStatus());
        System.out.println("Статус подзадачи изменен:" + taskManager.subtaskMap.get(subTaskSecond.getId()).getStatus());
        System.out.println("Статус эпика после update: " + epic.getStatus());

        subtaskToDelete.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Статус третьего эпика до update: " + epicThird.getStatus());
        taskManager.updateSubTask(subtaskToDelete, subtaskToDelete.getId());
        System.out.println("Статус третьего эпика после update: " + epicThird.getStatus());

        taskManager.deleteTaskById(taskFirst.getId());
        System.out.println("После удаления задачи " + taskManager.getAllTasks());

        taskManager.deleteEpicById(epicSecond.getId());
        System.out.println("После удаления эпика: " + taskManager.getAllEpics());

        taskManager.deleteAllEpicSubTasks(epic);
        System.out.println("После удаления подзадач: " + epic.getStatus() + " " + epic.getSubTasks());

        taskManager.deleteSubTaskById(subtaskToDelete.getId());
        System.out.println("Подзадачи третьего эпика после удаления 1й задачи: " + taskManager.getEpicSubTasks(epicThird));
    }
}
