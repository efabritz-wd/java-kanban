package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task taskFirst = new Task("сортировка мусора", "сортировать мусор по категориям");
        Task taskSecond = new Task("собрать документы", "найти и собрать документы");
        taskManager.createTask(taskFirst);
        taskManager.createTask(taskSecond);
        System.out.println(taskManager.getAllTasks());

        Epic epic = new Epic("Поездка домой");
        taskManager.createEpic(epic);

        SubTask subtaskFirst = new SubTask("Билеты", "Заказать билеты", epic.getId());
        SubTask subTaskSecond =  new SubTask("Документы", "Проверить документы", epic.getId());
        SubTask subTaskSecondFirst =  new SubTask("Документы 2", "Проверить документы", epic.getId());

        taskManager.createSubTask(subtaskFirst);
        taskManager.createSubTask(subTaskSecond);
        taskManager.createSubTask(subTaskSecondFirst);

        Epic epicSecond = new Epic("Кружки");
        taskManager.createEpic(epicSecond);
        SubTask subTaskThird = new SubTask("Купить инструмент", "Выбрать и заказать инструмент", epicSecond.getId());
        taskManager.createSubTask(subTaskThird);

        Epic epicThird = new Epic("Тест");
        taskManager.createEpic(epicThird);
        SubTask subtaskToDelete = new SubTask("Пеовый тест", "Провести первый тест", epicThird.getId());
        taskManager.createSubTask(subtaskToDelete);
        SubTask subtaskToDeleteSecond = new SubTask("Второй тест", "Провести второй тест", epicThird.getId());
        taskManager.createSubTask(subtaskToDeleteSecond);

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());

        for(Epic epicVal : taskManager.getAllEpics()) {
            System.out.println("Эпик: " + epicVal);
            System.out.println("Подзадачи: " + taskManager.getEpicSubTasks(epicVal));
        }

        taskFirst.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskFirst);
        System.out.println("Статус задачи изменен:" + taskManager.getAllTasks().get(taskFirst.getId()).getStatus());

        subtaskFirst.setStatus(TaskStatus.DONE);
        subTaskSecond.setStatus(TaskStatus.DONE);
        System.out.println("Статус эпика до update: " + epic.getStatus());
        taskManager.updateSubTask(subtaskFirst);
        taskManager.updateSubTask(subTaskSecond);
        System.out.println("Статус подзадачи изменен:" + taskManager.getAllSubTasks().get(subtaskFirst.getId()).getStatus());
        System.out.println("Статус подзадачи изменен:" + taskManager.getAllSubTasks().get(subTaskSecond.getId()).getStatus());
        System.out.println("Статус эпика после update: " + epic.getStatus());

        taskManager.getAllEpics();
        taskManager.getEpicById(epicSecond.getId());
        taskManager.getEpicSubTasks(epic);

        printAllTasks(taskManager);

        subtaskToDelete.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Статус третьего эпика до update: " + epicThird.getStatus());
        taskManager.updateSubTask(subtaskToDelete);
        System.out.println("Статус третьего эпика после update: " + epicThird.getStatus());

        subtaskToDelete.setStatus(TaskStatus.NEW);
        System.out.println("Статус третьего эпика до update 2: " + epicThird.getStatus());
        taskManager.updateSubTask(subtaskToDelete);
        System.out.println("Статус третьего эпика после update 2: " + epicThird.getStatus());

        int idFirst = taskFirst.getId();
        taskManager.deleteTaskById(idFirst);
        System.out.println("После удаления задачи " + idFirst + ": " + taskManager.getAllTasks());
        showHistory();

        int idSecond = epicSecond.getId();
        taskManager.deleteEpicById(idSecond);
        System.out.println("После удаления эпика "  + idSecond + ": " + taskManager.getAllEpics());
        showHistory();

        int idThird = epic.getId();
        taskManager.deleteAllEpicSubTasks(epic);
        System.out.println("После удаления подзадач "+ idThird + ": " + epic.getStatus() + " " + epic.getSubTasks());
        showHistory();

        int idFourth = subtaskToDelete.getId();
        taskManager.deleteSubTaskById(idFourth);
        System.out.println("Подзадачи третьего эпика после удаления 1й задачи " + idFourth + ": " + taskManager.getEpicSubTasks(epicThird));
        showHistory();

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTasks((Epic)epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        showHistory();
    }

    public static void showHistory() {
        System.out.println("История:");
        for (Task task : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }
}
