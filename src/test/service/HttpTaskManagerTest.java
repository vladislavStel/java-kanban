package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private final Gson gson = new Gson();
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Subtask subTask1Epic1;
    private Subtask subTask2Epic1;
    private KVServer kvServer;
    private HistoryManager historyManager;

    @BeforeEach
    public void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        task1 = new Task("task1Name",
                "task1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 5, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(task1);
        taskManager.getTaskById(task1.getId());
        task2 = new Task("task2Name",
                "task2Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 5, 3, 10, 0)),
                Optional.of(Duration.ofMinutes(60)));
        taskManager.addNewTask(task2);
        taskManager.getTaskById(task2.getId());
        epic1 = new Epic("Epic1Name", "Epic1Description", Status.NEW);
        taskManager.addNewTask(epic1);
        taskManager.getEpicById(epic1.getId());
        subTask1Epic1 = new Subtask(epic1.getId(), "subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        taskManager.getSubtaskById(subTask1Epic1.getId());
        subTask2Epic1 = new Subtask(epic1.getId(),"subTask2Epic1Name",
                "subTask2Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 3, 10, 0)),
                Optional.of(Duration.ofMinutes(60)));
        taskManager.addNewTask(subTask2Epic1);
        taskManager.getSubtaskById(subTask2Epic1.getId());
    }

    @AfterEach
    public void stop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void save() {
        Type type;
        Optional<Map<Integer, Task>> tasks = Optional.empty();
        Optional<Map<Integer, Epic>> epics = Optional.empty();
        Optional<Map<Integer, Subtask>> subtasks = Optional.empty();
        Optional<List<Task>> history = Optional.empty();
        if (kvServer.getData().containsKey("task")) {
            type = new TypeToken<Map<Integer, Task>>() {}.getType();
            tasks = Optional.of(gson.fromJson(kvServer.getData().get("task"), type));
        }
        if (kvServer.getData().containsKey("epic")) {
            type = new TypeToken<Map<Integer, Epic>>() {}.getType();
            epics = Optional.of(gson.fromJson(kvServer.getData().get("epic"), type));
        }
        if (kvServer.getData().containsKey("subtask")) {
            type = new TypeToken<Map<Integer, Subtask>>() {}.getType();
            subtasks = Optional.of(gson.fromJson(kvServer.getData().get("subtask"), type));
        }
        if (kvServer.getData().containsKey("history")) {
            type = new TypeToken<List>() {}.getType();
            history = Optional.of(gson.fromJson(kvServer.getData().get("history"), type));
        }
        assertEquals(10, tasks.get().size() +
                epics.get().size() +
                subtasks.get().size() +
                history.get().size());
    }

    @Test
    void loadFromServer() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deletesAllSubtasks();
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        historyManager.remove(4);
        historyManager.remove(5);
    }
}