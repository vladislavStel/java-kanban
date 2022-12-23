package Kanban.model;

public class IdTasks {
    private int idCounter;

    public int getIdCounter() {
        return ++idCounter;
    }
}