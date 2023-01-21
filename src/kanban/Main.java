package kanban;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Status;
import kanban.model.Task;
import kanban.service.Managers;
import kanban.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task №1", "Описание", Status.NEW);
        Task task2 = new Task("Task №2", "Описание", Status.NEW);

        Epic epic1 = new Epic("Epic №1", "Описание");
        Epic epic2 = new Epic("Epic №2", "Описание");
        Epic epic3 = new Epic("Epic №3", "Описание");

        // ДОБАВЛЯЕМ TASKS
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        int ID_task1 = task1.getId();
        int ID_task2 = task2.getId();
        System.out.println("Task №1 и №2 добавлены");
        System.out.println("------------------------------------------------");

        // ДОБАВЛЯЕМ EPICS
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);
        taskManager.addNewTask(epic3);
        System.out.println("Epic № 1, 2, 3 добавлены");
        System.out.println("------------------------------------------------");

        // ДОБАВЛЯЕМ SUBTASKS
        int ID_epic1 = epic1.getId();
        int ID_epic2 = epic2.getId();
        int ID_epic3 = epic3.getId();

        Subtask subtask1 = new Subtask(ID_epic1, "Subtask №1", "Описание", Status.NEW);
        Subtask subtask2 = new Subtask(ID_epic1, "Subtask №2", "Описание", Status.NEW);
        Subtask subtask3 = new Subtask(ID_epic2, "Subtask №3", "Описание", Status.NEW);

        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);
        int ID_subtask1 = subtask1.getId();
        int ID_subtask2 = subtask2.getId();
        int ID_subtask3 = subtask3.getId();
        System.out.println("Subtasks добавлены");
        System.out.println("------------------------------------------------");
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ СПИСКИ TASKS, EPICS, SUBTASKS
        System.out.println("Получаем список Tasks:");
        System.out.println(taskManager.getTasks());
        System.out.println("------------------------------------------------");

        System.out.println("Получаем список Epics:");
        System.out.println(taskManager.getEpics());
        System.out.println("------------------------------------------------");

        System.out.println("Получаем список Subtasks:");
        System.out.println(taskManager.getSubtasks());
        System.out.println("------------------------------------------------");
        System.out.println("------------------------------------------------");

        // ОБНОВЛЯЕМ STATUS, DESCRIPTION и NAME TASKS
        System.out.println("Меняем данные Task №1");
        Task newTask1 = new Task("Task №1", "Новое описание", Status.IN_PROGRESS);
        newTask1.setId(ID_task1);
        taskManager.updateTask(newTask1);
        System.out.println("Меняем статус Task №2 с NEW на DONE");
        Task newTask2 = taskManager.getTaskById(ID_task2);
        newTask2.setStatus(Status.DONE);
        taskManager.updateTask(newTask2);
        System.out.println(taskManager.getTasks());
        System.out.println("------------------------------------------------");

        // ПРОВЕРЯЕМ ОБНОВЛЕНИЕ СТАТУСОВ EPICS
        System.out.println("Меняем статус Subtask №1 с NEW на IN_PROGRESS");
        Subtask newSubtask1 = taskManager.getSubtaskById(ID_subtask1);
        newSubtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(newSubtask1);
        System.out.println("Меняем статус Subtask №3 с NEW на DONE");
        Subtask newSubtask3 = taskManager.getSubtaskById(ID_subtask3);
        newSubtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(newSubtask3);
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ SUBTASKS ПО ID EPICS
        System.out.println("Получаем Subtask - Epic1, Epic2, Epic3");
        System.out.println(taskManager.getSubtasksInEpic(ID_epic1));
        System.out.println(taskManager.getSubtasksInEpic(ID_epic2));
        System.out.println(taskManager.getSubtasksInEpic(ID_epic3));
        System.out.println("------------------------------------------------");

        // ПОЛУЧАЕМ ВСЕ TASK, EPIC, SUBTASK ПО ID
        System.out.println("Проверяем task, epic, subtask по ID");
        System.out.println(taskManager.getTaskById(ID_task1));
        System.out.println(taskManager.getEpicById(ID_epic3));
        System.out.println(taskManager.getSubtaskById(ID_subtask2));
        System.out.println("------------------------------------------------");

        // ИСТОРИЯ ПРОСМОТРОВ

        // ДАННЫЕ ДЛЯ ПРОСМОТРА:
        System.out.println(taskManager.getTaskById(ID_task1));
        System.out.println(taskManager.getTaskById(ID_task1));

        System.out.println(taskManager.getEpicById(ID_epic1));
        System.out.println(taskManager.getEpicById(ID_epic1));
        System.out.println(taskManager.getEpicById(ID_epic3));

        System.out.println(taskManager.getSubtaskById(ID_subtask1));
        System.out.println(taskManager.getSubtaskById(ID_subtask1));

        System.out.println(taskManager.getTaskById(ID_task1));
        System.out.println(taskManager.getEpicById(ID_epic1));
        System.out.println(taskManager.getSubtaskById(ID_subtask1));
        System.out.println("------------------------------------------------");
        System.out.println("История просмотров:");
        for (int i = 0; i < taskManager.getHistory().getHistoryViewTask().size(); i++) {
            System.out.println("№" + (i + 1) + " - " + taskManager.getHistory().getHistoryViewTask().get(i));
        }
        System.out.println("------------------------------------------------");

        // УДАЛЯЕМ TASK, EPIC, SUBTASK ПО ID
        System.out.println("Удаляем по одной task, epic, subtask");
        taskManager.deleteTaskById(ID_task1);
        taskManager.deleteEpicById(ID_epic3);
        taskManager.deleteSubtaskById(ID_subtask2);
        System.out.println("Проверяем удаление...");

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("------------------------------------------------");

        // ПРОВЕРЯЕМ ИСТОРИЮ ПРОСМОТРОВ ПОСЛЕ УДАЛЕНИЯ
        System.out.println("История просмотров:");
        for (int i = 0; i < taskManager.getHistory().getHistoryViewTask().size(); i++) {
            System.out.println("№" + (i + 1) + " - " + taskManager.getHistory().getHistoryViewTask().get(i));
        }
        System.out.println("------------------------------------------------");

        // УДАЛЯЕМ ВСЕ TASK, EPIC, SUBTASK
        System.out.println("Удаляем все tasks, epics, subtasks...");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deletesAllSubtasks();
        System.out.println("Проверяем...");

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("------------------------------------------------");

        // ПРОВЕРЯЕМ ИСТОРИЮ ПРОСМОТРОВ ПОСЛЕ ПОЛНОГО УДАЛЕНИЯ
        System.out.println("История просмотров:");
        for (int i = 0; i < taskManager.getHistory().getHistoryViewTask().size(); i++) {
            System.out.println("№" + (i + 1) + " - " + taskManager.getHistory().getHistoryViewTask().get(i));
        }
        System.out.println("------------------------------------------------");
    }
}