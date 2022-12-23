package Kanban;

import Kanban.model.Epic;
import Kanban.model.Subtask;
import Kanban.model.TasksStatus;
import Kanban.service.TaskManager;
import Kanban.model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager managerTasks = new TaskManager();

        Task task1 = new Task("Task №1", "Описание", TasksStatus.NEW);
        Task task2 = new Task("Task №2", "Описание", TasksStatus.NEW);

        Epic epic1 = new Epic("Epic №1", "Описание");
        Epic epic2 = new Epic("Epic №2", "Описание");
        Epic epic3 = new Epic("Epic №3", "Описание");

        // ДОБАВЛЯЕМ TASKS
        managerTasks.addNewTask(task1);
        managerTasks.addNewTask(task2);
        int ID_task1 = task1.getId();
        int ID_task2 = task2.getId();
        System.out.println("Task №1 и №2 добавлены");
        System.out.println("------------------------------------------------");

        // ДОБАВЛЯЕМ EPICS
        managerTasks.addNewTask(epic1);
        managerTasks.addNewTask(epic2);
        managerTasks.addNewTask(epic3);
        System.out.println("Epic № 1, 2, 3 добавлены");
        System.out.println("------------------------------------------------");

        // ДОБАВЛЯЕМ SUBTASKS
        int ID_epic1 = epic1.getId();
        int ID_epic2 = epic2.getId();
        int ID_epic3 = epic3.getId();

        Subtask subtask1 = new Subtask("Subtask №1", "Описание", TasksStatus.NEW, ID_epic1);
        Subtask subtask2 = new Subtask("Subtask №2", "Описание", TasksStatus.NEW, ID_epic1);
        Subtask subtask3 = new Subtask("Subtask №3", "Описание", TasksStatus.NEW, ID_epic2);

        managerTasks.addNewTask(subtask1);
        managerTasks.addNewTask(subtask2);
        managerTasks.addNewTask(subtask3);
        int ID_subtask1 = subtask1.getId();
        int ID_subtask2 = subtask2.getId();
        int ID_subtask3 = subtask3.getId();
        System.out.println("Subtasks добавлены");
        System.out.println("------------------------------------------------");
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ СПИСКИ TASKS, EPICS, SUBTASKS
        System.out.println("Получаем список Tasks:");
        System.out.println(managerTasks.getTasks());
        System.out.println("------------------------------------------------");

        System.out.println("Получаем список Epics:");
        System.out.println(managerTasks.getEpics());
        System.out.println("------------------------------------------------");

        System.out.println("Получаем список Subtasks:");
        System.out.println(managerTasks.getSubtasks());
        System.out.println("------------------------------------------------");
        System.out.println("------------------------------------------------");

        // ОБНОВЛЯЕМ STATUS, DESCRIPTION и NAME TASKS
        System.out.println("Меняем данные Task №1");
        Task newTask1 = new Task("Task №1", "Новое описание", TasksStatus.IN_PROGRESS);
        newTask1.setId(ID_task1);
        managerTasks.updateTask(newTask1);
        System.out.println("Меняем статус Task №2 с NEW на DONE");
        Task newTask2 = managerTasks.getTaskById(ID_task2);
        newTask2.setStatus(TasksStatus.DONE);
        managerTasks.updateTask(newTask2);
        System.out.println(managerTasks.getTasks());
        System.out.println("------------------------------------------------");

        // ПРОВЕРЯЕМ ОБНОВЛЕНИЕ СТАТУСОВ EPICS
        System.out.println("Меняем статус Subtask №1 с NEW на IN_PROGRESS");
        Subtask newSubtask1 = managerTasks.getSubtaskById(ID_subtask1);
        newSubtask1.setStatus(TasksStatus.IN_PROGRESS);
        managerTasks.updateSubtask(newSubtask1);
        System.out.println("Меняем статус Subtask №3 с NEW на DONE");
        Subtask newSubtask3 = managerTasks.getSubtaskById(ID_subtask3);
        newSubtask3.setStatus(TasksStatus.DONE);
        managerTasks.updateSubtask(newSubtask3);
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ SUBTASKS ПО ID EPICS
        System.out.println("Получаем Subtask - Epic1, Epic2, Epic3");
        System.out.println(managerTasks.getSubtasksInEpic(ID_epic1));
        System.out.println(managerTasks.getSubtasksInEpic(ID_epic2));
        System.out.println(managerTasks.getSubtasksInEpic(ID_epic3));
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ ВСЕ TASK, EPIC, SUBTASK ПО ID
        System.out.println("Проверяем task, epic, subtask по ID");
        System.out.println(managerTasks.getTaskById(ID_task1));
        System.out.println(managerTasks.getEpicById(ID_epic3));
        System.out.println(managerTasks.getSubtaskById(ID_subtask2));
        System.out.println("------------------------------------------------");

        // УДАЛЯЕМ TASK, EPIC, SUBTASK ПО ID
        System.out.println("Удаляем по одной task, epic, subtask");
        managerTasks.deleteTaskById(ID_task1);
        managerTasks.deleteEpicById(ID_epic3);
        managerTasks.deleteSubtaskById(ID_subtask2);
        System.out.println("Проверяем удаление...");

        System.out.println(managerTasks.getTasks());
        System.out.println(managerTasks.getEpics());
        System.out.println(managerTasks.getSubtasks());
        System.out.println("------------------------------------------------");

        // УДАЛЯЕМ ВСЕ TASK, EPIC, SUBTASK
        System.out.println("Удаляем все tasks, epics, subtasks...");
        managerTasks.deleteAllTasks();
        managerTasks.deleteAllEpics();
        managerTasks.deletesAllSubtasks();
        System.out.println("Проверяем...");

        System.out.println(managerTasks.getTasks());
        System.out.println(managerTasks.getEpics());
        System.out.println(managerTasks.getSubtasks());
        System.out.println("------------------------------------------------");
    }
}