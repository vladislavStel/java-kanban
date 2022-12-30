package kanban.service;

import java.util.List;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

public interface TaskManager {

    void addNewTask(Task task);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deletesAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Integer> getSubtasksInEpic(int id);

    void updateStatusEpic(int id);
}