package kanban.service;

import kanban.model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final TaskIdCounter idCounter;
    private final HistoryManager history;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        idCounter = new TaskIdCounter();
        history = Managers.getDefaultHistory();
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdEpic())) {
                subtasks.put(subtask.getId(), subtask);
                updateStatusEpic(subtask.getIdEpic());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtask : epic.getSubtasksIdInEpic()) {
                history.remove(subtask);
                subtasks.remove(subtask);
            }
            epics.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int idEpic = subtask.getIdEpic();
            Epic epic = epics.get(idEpic);
            epic.deleteSubtask(id);
            subtasks.remove(id);
            updateStatusEpic(subtask.getIdEpic());
            history.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer key : tasks.keySet()) {
            history.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deletesAllSubtasks();
        for (Integer key : epics.keySet()) {
            history.remove(key);
        }
        epics.clear();
    }

    @Override
    public void deletesAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            updateStatusEpic(epic.getId());
        }
        for (Integer key : subtasks.keySet()) {
            history.remove(key);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        history.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        history.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        history.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Integer> getSubtasksInEpic(int id) {
        if (epics.get(id) != null) {
            return epics.get(id).getSubtasksIdInEpic();
        } else {
            return new ArrayList<>();
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

    @Override
    public void updateStatusEpic(int id) {
        Epic epic = new Epic(epics.get(id).getName(), epics.get(id).getDescription(), id);
        epic.setSubtasksIdInEpic(epics.get(id).getSubtasksIdInEpic());

        int statusNewCounter = 0;
        int statusInProgressCounter = 0;
        int statusDoneCounter = 0;
        if (epic.getSubtasksIdInEpic() != null) {
            for (int i = 0; i < epic.getSubtasksIdInEpic().size(); i++) {
                if (subtasks.get(epic.getSubtasksIdInEpic().get(i)) != null) {
                    if (subtasks.get(epic.getSubtasksIdInEpic().get(i)).getStatus() == Status.NEW) {
                        statusNewCounter++;
                    } else if (subtasks.get(epic.getSubtasksIdInEpic().get(i)).getStatus() == Status.IN_PROGRESS) {
                        statusInProgressCounter++;
                    } else {
                        statusDoneCounter++;
                    }
                }
            }

            if (statusInProgressCounter == 0 && statusDoneCounter == 0) {
                epic.setStatus(Status.NEW);
            } else if (statusDoneCounter > 0 || (statusNewCounter < 1 && statusInProgressCounter < 1)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        } else {
            epic.setStatus(Status.NEW);
        }
        epics.put(epic.getId(), epic);
    }

    public HistoryManager getHistory() {
        return history;
    }
}