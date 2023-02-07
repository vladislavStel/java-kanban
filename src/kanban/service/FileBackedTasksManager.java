package kanban.service;

import kanban.model.*;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileManager = Managers.getDefaultFileBackedTasksManager();

        Task task1 = new Task("Задача 1", "Описание");
        fileManager.addNewTask(task1);
        int ID_task1 = task1.getId();
        System.out.println("Task №1 создан.");

        Task task2 = new Task("Задача 2", "Описание");
        fileManager.addNewTask(task2);
        int ID_task2 = task2.getId();
        System.out.println("Task №2 создан.");
        System.out.println("-----------------------------------------------------------------------------------------");

        Epic epic1 = new Epic("Эпик 1", "Описание");
        fileManager.addNewTask(epic1);
        int ID_epic1 = epic1.getId();
        System.out.println("Epic №1 создан.");

        Epic epic2 = new Epic("Эпик 2", "Описание");
        fileManager.addNewTask(epic2);
        int ID_epic2 = epic2.getId();
        System.out.println("Epic №2 создан.");
        System.out.println("-----------------------------------------------------------------------------------------");

        Subtask subtask1 = new Subtask(ID_epic1, "Сабтаск 1", "Описание");
        fileManager.addNewTask(subtask1);
        int ID_subtask1 = subtask1.getId();
        System.out.println("Subtask №1 добавлен к эпику №1.");

        Subtask subtask2 = new Subtask(ID_epic1, "Сабтаск 2", "Описание");
        fileManager.addNewTask(subtask2);
        int ID_subtask2 = subtask2.getId();
        System.out.println("Subtask №2 добавлен к эпику №1.");

        Subtask subtask3 = new Subtask(ID_epic1, "Сабтаск 3", "Описание");
        fileManager.addNewTask(subtask3);
        int ID_subtask3 = subtask3.getId();
        System.out.println("Subtask №3 добавлен к эпику №1.");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (Task key : fileManager.getTasks()) {
            key.setStatus(Status.DONE);
        }
        System.out.println("Устанавливаем статус DONE всем таскам.");

        for (Integer key : fileManager.getSubtasksInEpic(ID_epic1)) {
            fileManager.getSubtaskById(key).setStatus(Status.DONE);
            fileManager.updateStatusEpic(ID_epic1);
        }
        System.out.println("Устанавливаем статус DONE всем сабтаскам эпика 1.");
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем Таск №1");
        fileManager.getTaskById(ID_task1);
        System.out.println("Получаем Таск №2");
        fileManager.getTaskById(ID_task2);
        System.out.println("Получаем Эпик №1");
        fileManager.getEpicById(ID_epic1);
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем историю:");
        System.out.println(fileManager.getHistory().getHistoryViewTask());
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем Эпик №2");
        fileManager.getEpicById(ID_epic2);
        System.out.println("Получаем Таск №2");
        fileManager.getTaskById(ID_task2);
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем историю");
        System.out.println(fileManager.getHistory().getHistoryViewTask());
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем Сабтаск №3");
        fileManager.getSubtaskById(ID_subtask3);
        System.out.println("Получаем Сабтаск №2");
        fileManager.getSubtaskById(ID_subtask2);
        System.out.println("Получаем Сабтаск №1");
        fileManager.getSubtaskById(ID_subtask1);
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем историю");
        System.out.println(fileManager.getHistory().getHistoryViewTask());
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Восстанавливаем состояние менеджера из файла");
        TaskManager fileManager2 = loadFromFile(new File("src/kanban/saveInstanceState.csv"));
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Получаем историю");
        System.out.println(fileManager2.getHistory().getHistoryViewTask());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        int maxIdTask = 0;
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (FileReader reader = new FileReader(file.toString());
             BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    line = br.readLine();
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
                    Task task = new Task(split[2], split[3], Status.valueOf(split[4]));
                    task.setId(Integer.parseInt(split[0]));
                    fileBackedTasksManager.addNewTask(task);
                    if (task.getId() > maxIdTask) {
                        maxIdTask = task.getId();
                    }
                } else if ((Type.EPIC.toString().equals(split[1]))) {
                    Epic epic = new Epic(split[2], split[3], Status.valueOf(split[4]));
                    epic.setId(Integer.parseInt(split[0]));
                    fileBackedTasksManager.addNewTask(epic);
                    if (epic.getId() > maxIdTask) {
                        maxIdTask = epic.getId();
                    }
                } else if ((Type.SUBTASK.toString().equals(split[1]))) {
                    Subtask subtask = new Subtask(Integer.parseInt(split[5]), split[2], split[3], Status.valueOf(split[4]));
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
        return fileBackedTasksManager;
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(file.toString())) {
            fileWriter.write("id,type,name,description,status,epic");
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