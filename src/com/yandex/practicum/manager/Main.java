package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task taskFirst = new Task(taskManager.getNextId(), "сортировка мусора", "сортировать мусор по категориям");
        Task taskSecond = new Task(taskManager.getNextId(), "собрать документы", "найти и собрать документы");
        taskManager.createTask(taskFirst);
        taskManager.createTask(taskSecond);
        System.out.println(taskManager.getAllTasks());

        Epic epic = new Epic(taskManager.getNextId(), "Поездка домой");
        taskManager.createEpic(epic);

        SubTask subtaskFirst = new SubTask(taskManager.getNextId(), "Билеты", "Заказать билеты", epic.getId());
        SubTask subTaskSecond =  new SubTask(taskManager.getNextId(),"Документы", "Проверить документы", epic.getId());

        taskManager.createSubTask(subtaskFirst);
        taskManager.createSubTask(subTaskSecond);

        Epic epicSecond = new Epic(taskManager.getNextId(), "Кружки");
        taskManager.createEpic(epicSecond);
        SubTask subTaskThird = new SubTask(taskManager.getNextId(), "Купить инструмент", "Выбрать и заказать инструмент", epicSecond.getId());
        taskManager.createSubTask(subTaskThird);

        Epic epicThird = new Epic(taskManager.getNextId(), "Тест");
        taskManager.createEpic(epicThird);
        SubTask subtaskToDelete = new SubTask(taskManager.getNextId(), "Пеовый тест", "Провести первый тест", epicThird.getId());
        taskManager.createSubTask(subtaskToDelete);
        SubTask subtaskToDeleteSecond = new SubTask(taskManager.getNextId(), "Второй тест", "Провести второй тест", epicThird.getId());
        taskManager.createSubTask(subtaskToDeleteSecond);

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());

        for(Epic epicVal : taskManager.epicMap.values()) {
            System.out.println("Эпик: " + epicVal);
            System.out.println("Подзадачи: " + taskManager.getEpicSubTasks(epicVal));
        }

        taskFirst.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskFirst);
        System.out.println("Статус задачи изменен:" + taskManager.taskMap.get(taskFirst.getId()).getStatus());

        subtaskFirst.setStatus(TaskStatus.DONE);
        subTaskSecond.setStatus(TaskStatus.DONE);
        System.out.println("Статус эпика до update: " + epic.getStatus());
        taskManager.updateSubTask(subtaskFirst);
        taskManager.updateSubTask(subTaskSecond);
        System.out.println("Статус подзадачи изменен:" + taskManager.subtaskMap.get(subtaskFirst.getId()).getStatus());
        System.out.println("Статус подзадачи изменен:" + taskManager.subtaskMap.get(subTaskSecond.getId()).getStatus());
        System.out.println("Статус эпика после update: " + epic.getStatus());

        subtaskToDelete.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Статус третьего эпика до update: " + epicThird.getStatus());
        taskManager.updateSubTask(subtaskToDelete);
        System.out.println("Статус третьего эпика после update: " + epicThird.getStatus());

        subtaskToDelete.setStatus(TaskStatus.NEW);
        System.out.println("Статус третьего эпика до update 2: " + epicThird.getStatus());
        taskManager.updateSubTask(subtaskToDelete);
        System.out.println("Статус третьего эпика после update 2: " + epicThird.getStatus());

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
