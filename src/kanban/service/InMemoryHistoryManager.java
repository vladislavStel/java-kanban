package kanban.service;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 9;

    private final List<Task> historyViewTask;

    public InMemoryHistoryManager() {
        this.historyViewTask = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        while (historyViewTask.size() > HISTORY_SIZE) {
            historyViewTask.remove(0);
        }
        historyViewTask.add(task);
    }

    @Override
    public List<Task> getHistoryViewTask() {
        return historyViewTask;
    }
}
