package kanban.service;

public class TaskIdCounter {
    private int idCounter;

    public int getIdCounter() {
        return ++idCounter;
    }
}