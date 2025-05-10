package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class UseCase {

    public static void start(String[] args) throws IOException {
        useCase2();
    }

    public static void useCase2() throws IOException {
        File file = new File("./data/tasks.csv");
        FileBackedTaskManager backupManager = (FileBackedTaskManager) Managers.getDefaultBackup(file);
        backupManager.resetFile();

        Task taskFirst = new Task("сортировка мусора", "сортировать мусор по категориям");
        Task taskSecond = new Task("собрать документы", "найти и собрать документы");
        taskFirst.setStartTime(LocalDateTime.now().plusHours(1));
        taskFirst.setDuration(Duration.ofMinutes(30));

        taskSecond.setStartTime(LocalDateTime.now().plusHours(1).plusMinutes(20));
        taskSecond.setDuration(Duration.ofMinutes(10));


        backupManager.createTask(taskFirst);
        backupManager.createTask(taskSecond);

        Epic epic = new Epic("Поездка домой", "Описание");
        backupManager.createEpic(epic);

        SubTask subtaskFirst = new SubTask("Билеты", "Заказать билеты", epic.getId());
        SubTask subTaskSecond = new SubTask("Документы", "Проверить документы", epic.getId());
        subtaskFirst.setStartTime(LocalDateTime.now().plusHours(2));
        subtaskFirst.setDuration(Duration.ofMinutes(10));

        subTaskSecond.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(30));
        subTaskSecond.setDuration(Duration.ofMinutes(10));

        backupManager.createSubTask(subtaskFirst);
        backupManager.createSubTask(subTaskSecond);
        FileBackedTaskManager backupManagerSecond = (FileBackedTaskManager) Managers.getDefaultBackup(new File("./data/tasks.csv"));
        List<String> lines = backupManagerSecond.readFromFile();
        for (String line : lines) {
            if (line.charAt(0) == 'i' && line.charAt(1) == 'd') {
                continue;
            }
            Task task = FileBackedTaskManager.fromString(line);
            System.out.println(task);
        }
    }

    public static void useCase1() {
        TaskManager taskManager = Managers.getDefault();
        Task taskFirst = new Task("сортировка мусора", "сортировать мусор по категориям");
        Task taskSecond = new Task("собрать документы", "найти и собрать документы");
        taskManager.createTask(taskFirst);
        taskManager.createTask(taskSecond);
        System.out.println(taskManager.getAllTasks());

        Epic epic = new Epic("Поездка домой");
        taskManager.createEpic(epic);

        SubTask subtaskFirst = new SubTask("Билеты", "Заказать билеты", epic.getId());
        SubTask subTaskSecond = new SubTask("Документы", "Проверить документы", epic.getId());
        SubTask subTaskSecondFirst = new SubTask("Документы 2", "Проверить документы", epic.getId());

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

        for (Epic epicVal : taskManager.getAllEpics()) {
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
        System.out.println("После удаления эпика " + idSecond + ": " + taskManager.getAllEpics());
        showHistory();

        int idThird = epic.getId();
        taskManager.deleteAllEpicSubTasks(epic);
        System.out.println("После удаления подзадач " + idThird + ": " + epic.getStatus() + " " + epic.getSubTasks());
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

            for (Task task : manager.getEpicSubTasks((Epic) epic)) {
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
