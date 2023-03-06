package service;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Managers {

    public static TaskManager getDefault() {
        try {
            return new HttpTaskManager(Managers.getDefaultHistory(), URI.create("http://localhost:8078"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}