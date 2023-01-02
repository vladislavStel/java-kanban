package kanban.service;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyViewTask;

    public InMemoryHistoryManager() {
        this.historyViewTask = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        while (historyViewTask.size() > 9) {
            historyViewTask.remove(0);
        }
        historyViewTask.add(task);
    }

    @Override
    public List<Task> getHistoryViewTask() {
        return historyViewTask;
    }
}
