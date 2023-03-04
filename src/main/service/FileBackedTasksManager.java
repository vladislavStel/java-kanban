package service;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public void loadFromFile(File file) {
        int maxIdTask = 0;
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(historyManager, file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    line = reader.readLine();
                    if (line == null) {
                        continue;
                    }
                    String[] split = line.split(",");
                    for (String str : split) {
                        int key = Integer.parseInt(str);
                        if (fileBackedTasksManager.tasks.containsKey(key)) {
                            fileBackedTasksManager.getTaskById(key);
                        } else if (fileBackedTasksManager.epics.containsKey(key)) {
                            fileBackedTasksManager.getEpicById(key);
                        } else if (fileBackedTasksManager.subtasks.containsKey(key)) {
                            fileBackedTasksManager.getSubtaskById(key);
                        }
                    }
                    break;
                }
                String[] split = line.split(",");
                if (Type.TASK.toString().equals(split[1])) {
                    Task task =new Task(split[2], split[4], Status.valueOf(split[3]),
                            Optional.of(LocalDateTime.parse(split[5], Task.FORMATTER)),
                            Optional.of(Duration.parse(split[6])));
                    fileBackedTasksManager.addNewTask(task);
                    if (task.getId() > maxIdTask) {
                        maxIdTask = task.getId();
                    }
                } else if ((Type.EPIC.toString().equals(split[1]))) {
                    Epic epic = new Epic(split[2], split[4], Status.valueOf(split[3]));
                    epic.setId(Integer.parseInt(split[0]));
                    fileBackedTasksManager.addNewTask(epic);
                    if (epic.getId() > maxIdTask) {
                        maxIdTask = epic.getId();
                    }
                } else if ((Type.SUBTASK.toString().equals(split[1]))) {
                    Subtask subtask = new Subtask(Integer.parseInt(split[7]), split[2], split[4],
                            Status.valueOf(split[3]),
                            Optional.of(LocalDateTime.parse(split[5], Task.FORMATTER)),
                            Optional.of(Duration.parse(split[6])));
                    subtask.setId(Integer.parseInt(split[0]));
                    fileBackedTasksManager.addNewTask(subtask);
                    if (subtask.getId() > maxIdTask) {
                        maxIdTask = subtask.getId();
                    }
                }
            }
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
        fileBackedTasksManager.idCounter.setIdCounter(maxIdTask);
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(file.toString())) {
            fileWriter.write("id,type,name,description,status,startTime,duration,epic");
            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    fileWriter.write("\n" + task.toString());
                }
            }
            if (!epics.isEmpty()) {
                for (Epic epic : epics.values()) {
                    fileWriter.write("\n" + epic.toString());
                }
            }
            if (!subtasks.isEmpty()) {
                for (Subtask subtask : subtasks.values()) {
                    fileWriter.write("\n" + subtask.toString());
                }
            }
            fileWriter.write("\n\n");
            if (!getHistory().getHistoryViewTask().isEmpty()) {
                ArrayList<String> idList = new ArrayList<>();
                for (Task task : getHistory().getHistoryViewTask()) {
                    idList.add(String.valueOf(task.getId()));
                }
                fileWriter.write(String.join(",", idList));
            }
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deletesAllSubtasks() {
        super.deletesAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }
}