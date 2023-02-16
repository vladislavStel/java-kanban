package service;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return new FileBackedTasksManager(new File("src/main/saveInstanceState.csv"));
    }
}