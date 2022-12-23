package Kanban.service;

import Kanban.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final IdTasks idCounter;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        idCounter = new IdTasks();
    }

    public void addNewTask(Task task) {
        switch (task.getType()) {
            case TASK:
                addTask(task);
                break;
            case EPIC:
                addEpic((Epic) task);
                break;
            case SUBTASK:
                addSubtask((Subtask) task);
                break;
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdEpic())) {
                subtasks.put(subtask.getId(), subtask);
                updateStatusEpic(subtask.getIdEpic());
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtask : epic.getSubtasksIdInEpic()) {
                subtasks.remove(subtask);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int idEpic = subtask.getIdEpic();
            Epic epic = epics.get(idEpic);
            epic.deleteSubtask(id);
            subtasks.remove(id);
            updateStatusEpic(subtask.getIdEpic());
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        deletesAllSubtasks();
        epics.clear();
    }

    public void deletesAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            updateStatusEpic(epic.getId());
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Integer> getSubtasksInEpic(int id) {
        if (epics.get(id) != null) {
            return epics.get(id).getSubtasksIdInEpic();
        } else {
            return null;
        }
    }

    private void addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            updateTask(task);
            return;
        }
        if (task.getId() == 0) {
            task.setId(idCounter.getIdCounter());
        }
        tasks.put(task.getId(), task);
    }

    private void addEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateEpic(epic);
            return;
        }
        if (epic.getSubtasksIdInEpic().isEmpty()) {
            if (epic.getId() == 0) {
                epic.setId(idCounter.getIdCounter());
            }
            epics.put(epic.getId(), epic);
        }
    }

    private void addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            updateSubtask(subtask);
            return;
        }
        if (epics.containsKey(subtask.getIdEpic())) {
            if (subtask.getId() == 0) {
                subtask.setId(idCounter.getIdCounter());
            }
            subtasks.put(subtask.getId(), subtask);
            getSubtasksInEpic(subtask.getIdEpic()).add(subtask.getId());
            updateStatusEpic(subtask.getIdEpic());
        }
    }

    public void updateStatusEpic(int id) {
        Epic epic = new Epic(epics.get(id).getName(), epics.get(id).getDescription(), id);
        epic.setSubtasksIdInEpic(epics.get(id).getSubtasksIdInEpic());

        int statusNewCounter = 0;
        int statusInProgressCounter = 0;
        int statusDoneCounter = 0;
        if (epic.getSubtasksIdInEpic() != null) {
            for (int i = 0; i < epic.getSubtasksIdInEpic().size(); i++) {
                if (subtasks.get(epic.getSubtasksIdInEpic().get(i)) != null) {
                    if (subtasks.get(epic.getSubtasksIdInEpic().get(i)).getStatus() == TasksStatus.NEW) {
                        statusNewCounter++;
                    } else if (subtasks.get(epic.getSubtasksIdInEpic().get(i)).getStatus() == TasksStatus.IN_PROGRESS) {
                        statusInProgressCounter++;
                    } else {
                        statusDoneCounter++;
                    }
                }
            }

            if (statusInProgressCounter == 0 && statusDoneCounter == 0) {
                epic.setStatus(TasksStatus.NEW);
            } else if (statusDoneCounter > 0 || (statusNewCounter < 1 && statusInProgressCounter < 1)) {
                epic.setStatus(TasksStatus.DONE);
            } else {
                epic.setStatus(TasksStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TasksStatus.NEW);
        }
        epics.put(epic.getId(), epic);
    }
}