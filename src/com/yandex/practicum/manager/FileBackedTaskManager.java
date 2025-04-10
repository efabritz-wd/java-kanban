package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.*;
import com.yandex.practicum.utils.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static File FILE;

    public FileBackedTaskManager(File file) {
        super();
        setBackupFile(file);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        TaskManager manager = Managers.getDefaultBackup(file);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            // reset manager and file
            if (!manager.getAllTasks().isEmpty()) {
                manager.deleteAllTasks();
            }

            if (!manager.getAllEpics().isEmpty()) {
                manager.deleteAllEpicsAndSubTasks();
            }

            resetFile();

            for (String taskLine : lines) {
                String[] lineArray = taskLine.split(",");
                List<String> linesList = Arrays.asList(lineArray);
                if (linesList.contains("TASK")) {
                    Task task = fromString(taskLine);
                    manager.createTask(task);
                } else if (linesList.contains("EPIC")) {
                    Epic epic = (Epic) fromString(taskLine);
                    manager.createEpic(epic);
                } else if (linesList.contains("SUBTASK")) {
                    SubTask subTask = (SubTask) fromString(taskLine);
                    manager.createSubTask(subTask);
                }
            }
            return (FileBackedTaskManager) manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
    }

    public static void setBackupFile(File file) {
        if (file == null) {
            FILE = new File("./data/tasks.csv");
            return;
        }
        FILE = file;
    }

    public static Task fromString(String value) {
        String[] linesArray = value.split(",");
        for (int i = 0; i < linesArray.length; i++) {
            linesArray[i] = linesArray[i].trim();
        }
        if (linesArray.length < 5) {
            return null;
        }
        if (linesArray[1] == null) {
            return null;
        }

        TaskType taskType = TaskType.TASK;
        String foundTaskType = linesArray[1];
        if (foundTaskType.equals("EPIC")) {
            taskType = TaskType.EPIC;
        } else if (foundTaskType.equals("SUBTASK")) {
            taskType = TaskType.SUBTASK;
        }

        TaskStatus taskStatus = TaskStatus.NEW;
        String foundStatusType = linesArray[3];
        if (foundStatusType.equals("DONE")) {
            taskStatus = TaskStatus.DONE;
        } else if (foundStatusType.equals("IN_PROGRESS")) {
            taskStatus = TaskStatus.IN_PROGRESS;
        }

        switch (taskType) {
            case TaskType.TASK:
                Task task = new Task(linesArray[2], linesArray[4]);
                task.setId(Integer.valueOf(linesArray[0]));
                task.setStatus(taskStatus);
                return task;
            case TaskType.EPIC:
                Epic epic = new Epic(linesArray[2], linesArray[4]);
                epic.setId(Integer.valueOf(linesArray[0]));
                epic.setStatus(taskStatus);
                return epic;
            case TaskType.SUBTASK:
                if (linesArray[5] == null) {
                    return null;
                }
                SubTask subTask = new SubTask(linesArray[2], linesArray[4], Integer.valueOf(linesArray[5]));
                subTask.setId(Integer.valueOf(linesArray[0]));
                subTask.setStatus(taskStatus);
                return subTask;
        }
        return null;
    }

    public static void resetFile() throws IOException {
        try (Writer fileWriter = new FileWriter(FILE, StandardCharsets.UTF_8, false)) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        createTaskInFile(task);
    }

    @Override
    public SubTask createSubTask(SubTask subtask) {
        SubTask subTask = super.createSubTask(subtask);
        createTaskInFile(subTask);
        return subTask;
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        createTaskInFile(epic);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            updateTasksInFile(task);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при обновлении файла", e);
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        try {
            updateTasksInFile(subtask);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при обновлении файла", e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            updateTasksInFile(epic);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при обновлении файла", e);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        deleteTaskFromFile(id);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        List<String> lines = new ArrayList<>();
        try {
            lines = readFromFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        HashMap<Integer, Task> linesMap = linesToHash(lines);
        List<Integer> keysToRemove = new ArrayList<>();
        for (Task task : linesMap.values()) {
            if (task.getType().equals(TaskType.TASK)) {
                keysToRemove.add(task.getId());
            }
        }

        for (Integer key : keysToRemove) {
            linesMap.remove(key);
        }

        updateFile(linesMap);
    }

    @Override
    public void deleteAllEpicsAndSubTasks() {
        super.deleteAllEpicsAndSubTasks();
        List<String> lines = new ArrayList<>();
        try {
            lines = readFromFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        HashMap<Integer, Task> linesMap = linesToHash(lines);
        List<Integer> keysToRemove = new ArrayList<>();
        for (Task task : linesMap.values()) {
            if (task.getType().equals(TaskType.EPIC) || task.getType().equals(TaskType.SUBTASK)) {
                keysToRemove.add(task.getId());
            }
        }

        for (Integer key : keysToRemove) {
            linesMap.remove(key);
        }

        updateFile(linesMap);
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        deleteTaskFromFile(id);
    }

    @Override
    public void deleteAllEpicSubTasks(Epic epic) {
        super.deleteAllEpicSubTasks(epic);
        List<String> lines = new ArrayList<>();
        try {
            lines = readFromFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка", e);
        }
        HashMap<Integer, Task> linesMap = linesToHash(lines);
        List<Integer> keysToRemove = new ArrayList<>();
        for (Task task : linesMap.values()) {
            if (task.getType().equals(TaskType.SUBTASK)) {
                SubTask subTask = (SubTask) task;
                int epicId = subTask.getEpic();
                if (epicId == epic.getId()) {
                    keysToRemove.add(task.getId());
                }
            }
        }
        for (Integer key : keysToRemove) {
            linesMap.remove(key);
        }

        updateFile(linesMap);
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        super.deleteAllEpicsAndSubTasks();
        List<String> lines = new ArrayList<>();
        try {
            lines = readFromFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка", e);
        }
        HashMap<Integer, Task> linesMap = linesToHash(lines);
        List<Integer> keysToRemove = new ArrayList<>();
        for (Task task : linesMap.values()) {
            if (task.getType().equals(TaskType.SUBTASK)) {
                keysToRemove.add(task.getId());
            }
        }

        for (Integer key : keysToRemove) {
            linesMap.remove(key);
        }


        updateFile(linesMap);
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        deleteTaskFromFile(id);
    }

    public String toString(Task task) {
        String line = "";
        switch (task.getType()) {
            case TaskType.TASK:
                line = task.getId() + "," + TaskType.TASK + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
                break;
            case TaskType.EPIC:
                line = task.getId() + "," + TaskType.EPIC + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
                break;
            case TaskType.SUBTASK:
                SubTask subTask = (SubTask) task;
                int epicId = subTask.getEpic();
                line = task.getId() + "," + TaskType.SUBTASK + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId;
                break;
        }
        return line;
    }

    public void createTaskInFile(Task task) {
        String taskLine = toString(task);
        try {
            writeToFile(taskLine);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e); //?
        }
    }

    public void writeToFile(String line) throws IOException {
        try (Writer fileWriter = new FileWriter(FILE, StandardCharsets.UTF_8, true)) {
            fileWriter.write(line + "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e);
        }
    }

    public List<String> readFromFile() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                lines.add(line);
            }

            reader.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        return lines;
    }

    public HashMap<Integer, Task> linesToHash(List<String> lines) {
        HashMap<Integer, Task> linesMap = new HashMap<>();
        for (String line : lines) {
            line = line.trim();
            if (line.length() < 2 || line.isBlank()) {
                continue;
            }
            if (line.charAt(0) == 'i' && line.charAt(1) == 'd') {
                continue;
            }
            Task task = fromString(line);
            linesMap.put(task.getId(), task);
        }
        return linesMap;
    }

    public void fillFile(List<Task> tasks) throws IOException {
        for (Task task : tasks) {
            String line = toString(task);
            writeToFile(line);
        }
    }

    public void updateTasksInFile(Task task) throws IOException {
        List<String> taskLines = readFromFile();
        HashMap<Integer, Task> hashMap = linesToHash(taskLines);
        for (Integer key : hashMap.keySet()) {
            if (Objects.equals(key, task.getId())) {
                hashMap.put(key, task);
            }
        }
        resetFile();
        List<Task> taskList = new ArrayList<>(hashMap.values());
        fillFile(taskList);
    }

    public void deleteTaskFromFile(int id) {
        List<String> lines = new ArrayList<>();
        try {
            lines = readFromFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        HashMap<Integer, Task> linesMap = linesToHash(lines);
        List<Integer> keyToRemove = new ArrayList<>();
        if (linesMap.get(id).getType().equals(TaskType.EPIC)) {
            for (Task task : linesMap.values()) {
                if (task.getType().equals(TaskType.SUBTASK)) {
                    SubTask subTask = (SubTask) task;
                    if (subTask.getEpic() == id) {
                        keyToRemove.add(subTask.getId());
                    }
                }
            }
        }
        for (Integer key : keyToRemove) {
            linesMap.remove(key);
        }
        linesMap.remove(id);
        updateFile(linesMap);
    }

    public void updateFile(HashMap<Integer, Task> linesMap) {
        try {
            resetFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании нового файла", e);
        }
        try {
            List<Task> taskList = new ArrayList<>(linesMap.values());
            fillFile(taskList);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e);
        }
    }
}