package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final HashMap<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        history.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> getHistoryViewTask() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        final Node lastNode = last;
        final Node newNode = new Node(lastNode, task, null);
        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasksList.add(node.task);
            node = node.next;
        }
        return tasksList;
    }

    private void removeNode(Node node) {
        if (node != null) {
            final Node next = node.next;
            final Node prev = node.prev;
            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
        }
    }

    static class Node {
        private Task task;
        private Node prev;
        private Node next;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}