package com.yandex.practicum.utils;

import java.util.Objects;

public class Node <T> {

    public T task;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node<T> node = (Node<T>) o;
        return Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(task);
    }

    public Node<T> next;
    public Node<T> prev;

    public Node(T task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }
}
