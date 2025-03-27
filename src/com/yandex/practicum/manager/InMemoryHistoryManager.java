package com.yandex.practicum.manager;

import com.yandex.practicum.tasks.Task;
import java.util.*;
import com.yandex.practicum.utils.*;

public class InMemoryHistoryManager implements HistoryManager {
  //  protected final List<Task> tasksHistory = new ArrayList<>();
  protected final Map<Integer, Node<Task>> tasksHistoryMap = new HashMap<>();
    protected final List<Node<Task>> tasksHistoryList = new LinkedList<>();

    public void linkLast() {
        if(tasksHistoryList.size() > 1) {
            Node<Task> lastNode = tasksHistoryList.get(tasksHistoryList.size() - 2);
            Node<Task> node = tasksHistoryList.getLast();
            node.prev = lastNode;
            lastNode.next = node;
        }
    }

    public ArrayList<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        for(Node<Task> node : tasksHistoryList) {
            tasksList.add(node.task);
        }
        return (ArrayList<Task>) tasksList;
    }

    public void removeNode(Node<Task> node) {
        tasksHistoryList.remove(node);
    }

    @Override
    public List<Task> getHistory() { return getTasks(); }
   // public List<Task> getHistory() { return new ArrayList<>(tasksHistory); }

    @Override
    public void addTaskToHistory(Task task) {
       /* if(tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        Task taskToSave = new Task(task.getTitle(), task.getDescription());
        taskToSave.setId(task.getId());
        taskToSave.setStatus(task.getStatus());
        tasksHistory.add(taskToSave);*/
        Task taskToSave = new Task(task.getTitle(), task.getDescription());
        taskToSave.setId(task.getId());
        taskToSave.setStatus(task.getStatus());
        if(tasksHistoryMap.containsKey(task.getId())) {
            removeNode(new Node<>(task));
        }

        tasksHistoryList.add(new Node<>(task));
        linkLast();
        tasksHistoryMap.put(task.getId(), new Node<>(task));
    }

    @Override
    public void remove(int id) {
     if(tasksHistoryMap.containsKey(id)) {
         Node<Task> node = tasksHistoryMap.get(id);
         tasksHistoryMap.remove(id);
         tasksHistoryList.remove(node);
     }
    }
}
