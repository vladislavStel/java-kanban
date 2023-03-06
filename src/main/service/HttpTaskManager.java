package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(HistoryManager historyManager, URI url) throws IOException, InterruptedException {
        super(historyManager);
        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(tasks);
        kvTaskClient.put("task", jsonTasks);
        String jsonEpics = gson.toJson(epics);
        kvTaskClient.put("epic", jsonEpics);
        String jsonSubtasks = gson.toJson(subtasks);
        kvTaskClient.put("subtask", jsonSubtasks);
        String jsonHistory = gson.toJson(getHistory());
        kvTaskClient.put("history", jsonHistory);
    }

    public void loadFromServer() {
        Type type;
        type = new TypeToken<Map<Integer, Task>>() {}.getType();
        Map<Integer, Task> tasks = gson.fromJson(kvTaskClient.load("task"), type);
        type = new TypeToken<Map<Integer, Epic>>() {}.getType();
        Map<Integer, Epic> epics = gson.fromJson(kvTaskClient.load("epic"), type);
        type = new TypeToken<Map<Integer, Subtask>>() {}.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(kvTaskClient.load("subtask"), type);
        type = new TypeToken<List<Task>>() {}.getType();
        List<Task> history = gson.fromJson(kvTaskClient.load("history"), type);
        for (Task task : tasks.values()) {
            addNewTask(task);
        }
        for (Epic epic : epics.values()) {
            addNewTask(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            addNewTask(subtask);
        }
        for (Task task : history) {
            if (getTasks().contains(task)) {
                getTaskById(task.getId());
            }
            if (getEpics().contains(task)) {
                getEpicById(task.getId());
            }
            if (getSubtasks().contains(task)) {
                getSubtaskById(task.getId());
            }
        }
    }
}