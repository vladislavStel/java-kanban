package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
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
        if (epics.containsKey(epic.getId())) {
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
                getSubtasksInEpic(subtask.getIdEpic()).add(subtask.getId());
                updateStatusEpic(subtask.getIdEpic());
                setStartTimeForEpic(subtask.getIdEpic());
                setEndTimeForEpic(subtask.getIdEpic());
                setDurationForEpic(subtask.getIdEpic());
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
            sortedByStartTimeTasks.remove(subtasks.get(id));
            Subtask subtask = subtasks.get(id);
            int idEpic = subtask.getIdEpic();
            Epic epic = epics.get(idEpic);
            epic.deleteSubtask(id);
            subtasks.remove(id);
            updateStatusEpic(subtask.getIdEpic());
            setStartTimeForEpic(subtask.getIdEpic());
            setEndTimeForEpic(subtask.getIdEpic());
            setDurationForEpic(subtask.getIdEpic());
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
            updateStatusEpic(epic.getId());
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
        if (validationUniqueTimeOfTask(task)) {
            tasks.put(task.getId(), task);
            sortedByStartTimeTasks.add(task);
        }
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
            if (validationUniqueTimeOfTask(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                sortedByStartTimeTasks.add(subtask);
                getSubtasksInEpic(subtask.getIdEpic()).add(subtask.getId());
                updateStatusEpic(subtask.getIdEpic());
                setStartTimeForEpic(subtask.getIdEpic());
                setEndTimeForEpic(subtask.getIdEpic());
                setDurationForEpic(subtask.getIdEpic());
            }
        }
    }

    @Override
    public void updateStatusEpic(int id) {
        epics.get(id).setSubtasksIdInEpic(epics.get(id).getSubtasksIdInEpic());

        int statusNewCounter = 0;
        int statusInProgressCounter = 0;
        int statusDoneCounter = 0;
        if (epics.get(id).getSubtasksIdInEpic() != null) {
            for (int i = 0; i < epics.get(id).getSubtasksIdInEpic().size(); i++) {
                if (subtasks.get(epics.get(id).getSubtasksIdInEpic().get(i)) != null) {
                    if (subtasks.get(epics.get(id).getSubtasksIdInEpic().get(i)).getStatus() == Status.NEW) {
                        statusNewCounter++;
                    } else if (subtasks.get(epics.get(id).getSubtasksIdInEpic().get(i)).getStatus() == Status.IN_PROGRESS) {
                        statusInProgressCounter++;
                    } else {
                        statusDoneCounter++;
                    }
                }
            }

            if (statusInProgressCounter == 0 && statusDoneCounter == 0) {
                epics.get(id).setStatus(Status.NEW);
            } else if (statusDoneCounter > 0 || (statusNewCounter < 1 && statusInProgressCounter < 1)) {
                epics.get(id).setStatus(Status.DONE);
            } else {
                epics.get(id).setStatus(Status.IN_PROGRESS);
            }
        } else {
            epics.get(id).setStatus(Status.NEW);
        }
        epics.put(epics.get(id).getId(), epics.get(id));
    }

    private void setStartTimeForEpic(int epicId) {
        epics.get(epicId).getSubtasksIdInEpic()
                .stream()
                .map(this::getSubtaskById)
                .map(Subtask::getStartTime)
                .map(Optional::get)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).ifPresent(epics.get(epicId)::setStartTime);
    }

    private void setEndTimeForEpic(int epicId) {
        epics.get(epicId).getSubtasksIdInEpic()
                .stream()
                .map(this::getSubtaskById)
                .map(x -> x.getEndTime().orElse(x.getStartTime().get()))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).ifPresent(epics.get(epicId)::setEndTime);
    }

    private void setDurationForEpic(int epicId) {
        epics.get(epicId).setDuration(epics.get(epicId).getSubtasksIdInEpic()
                .stream()
                .map(this::getSubtaskById)
                .map(Subtask::getDuration)
                .map(Optional::get)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus));
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