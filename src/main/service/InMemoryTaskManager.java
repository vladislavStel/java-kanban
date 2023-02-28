package service;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final TaskIdCounter idCounter;
    private final HistoryManager history;
    protected final TreeSet<Task> sortedByStartTimeTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        idCounter = new TaskIdCounter();
        history = Managers.getDefaultHistory();
        sortedByStartTimeTasks = new TreeSet<>(Task.comparator);
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
            if (validationUniqueTimeOfTask(task)) {
                sortedByStartTimeTasks.remove(tasks.get(task.getId()));
                tasks.put(task.getId(), task);
                sortedByStartTimeTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId()) && isEqualsSubtasks(epics.get(epic.getId()).getSubtasksInEpic(),
                epic.getSubtasksInEpic())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdEpic())) {
            if (validationUniqueTimeOfTask(subtask)) {
                sortedByStartTimeTasks.remove(subtasks.get(subtask.getId()));
                subtasks.put(subtask.getId(), subtask);
                sortedByStartTimeTasks.add(subtask);
                Epic epic = epics.get(subtask.getIdEpic());
                epic.setSubtasksInEpic(subtask);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            sortedByStartTimeTasks.remove(tasks.get(id));
            tasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : epic.getSubtasksInEpic()) {
                history.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            sortedByStartTimeTasks.remove(subtasks.get(id));
            Subtask subtask = subtasks.get(id);
            int idEpic = subtask.getIdEpic();
            Epic epic = epics.get(idEpic);
            epic.deleteSubtask(subtask);
            subtasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer key : tasks.keySet()) {
            sortedByStartTimeTasks.remove(tasks.get(key));
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
        }
        for (Integer key : subtasks.keySet()) {
            sortedByStartTimeTasks.remove(subtasks.get(key));
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
    public List<Subtask> getSubtasksInEpic(int id) {
        Epic epic = epics.get(id);
        return epic.getSubtasksInEpic();
    }

    private void addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        if (task.getId() == 0) {
            task.setId(idCounter.getIdCounter());
        }
        if (validationUniqueTimeOfTask(task)) {
            tasks.put(task.getId(), task);
            sortedByStartTimeTasks.add(task);
        }
    }

    private void addEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        if (epic.getSubtasksInEpic().isEmpty()) {
            if (epic.getId() == 0) {
                epic.setId(idCounter.getIdCounter());
            }
            epics.put(epic.getId(), epic);
        }
    }

    private void addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }
        if (epics.containsKey(subtask.getIdEpic())) {
            if (subtask.getId() == 0) {
                subtask.setId(idCounter.getIdCounter());
            }
            if (validationUniqueTimeOfTask(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                sortedByStartTimeTasks.add(subtask);
                Epic epic = epics.get(subtask.getIdEpic());
                epic.setSubtasksInEpic(subtask);
            }
        }
    }

    private boolean isEqualsSubtasks(List<Subtask> list1, List<Subtask> list2) {
        return (list1.isEmpty() && list2.isEmpty()) ||
                (list1.size() == list2.size());
    }

    private boolean validationUniqueTimeOfTask(Task task) {
        if (sortedByStartTimeTasks.isEmpty()) {
            return true;
        }
        Task taskBefore = sortedByStartTimeTasks.floor(task);   // max
        Task taskAfter = sortedByStartTimeTasks.ceiling(task);  // min
        if (taskBefore == null) {
            return task.getEndTime().get().isBefore(sortedByStartTimeTasks.first().getStartTime().get());
        }
        if (taskAfter == null) {
            return task.getStartTime().get().isAfter(sortedByStartTimeTasks.last().getEndTime().get());
        }
        return task.getStartTime().get().isAfter(taskBefore.getEndTime().get()) &&
                task.getEndTime().get().isBefore(taskAfter.getStartTime().get());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedByStartTimeTasks);
    }

    public HistoryManager getHistory() {
        return history;
    }
}