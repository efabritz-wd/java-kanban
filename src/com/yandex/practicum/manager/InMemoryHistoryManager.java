package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;

import java.util.*;

import com.yandex.practicum.utils.*;

public class InMemoryHistoryManager implements HistoryManager {

    protected final Map<Integer, Node<Task>> tasksHistoryMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public void linkLast(Node<Task> node) {
        if (head == null && tail == null) {
            head = node;
        } else if (head != null && tail == null) {
            head.next = node;
            tail = node;
            tail.prev = head;
        } else if (head != null && tail != null) {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public void removeNode(Node<Task> node) {

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        tasksHistoryMap.remove(node.task.getId());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasksList.add(node.task);
            node = node.next;
        }
        return tasksList;
    }

    @Override
    public void addTaskToHistory(Task task) {
        Task taskToSave = new Task(task.getTitle(), task.getDescription());
        taskToSave.setId(task.getId());
        taskToSave.setStatus(task.getStatus());
        if (tasksHistoryMap.containsKey(task.getId())) {
            removeNode(tasksHistoryMap.get(task.getId()));
        }

        Node taskNode = new Node(taskToSave);
        linkLast(taskNode);
        tasksHistoryMap.put(taskToSave.getId(), taskNode);
    }

    @Override
    public void remove(int id) {
        if (tasksHistoryMap.containsKey(id)) {
            Node<Task> node = tasksHistoryMap.get(id);
            tasksHistoryMap.remove(id);
            removeNode(node);
        }
    }

    @Override
    public void clearHistory() {
        tasksHistoryMap.clear();
    }
}
