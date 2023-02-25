package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task1 = new Task("task1Name",
            "task1Description",
            NEW,
            Optional.of(LocalDateTime.of(2022, 5, 1, 10, 0)),
            Optional.of(Duration.ofMinutes(60 * 24)));
    protected Task task2 = new Task("task2Name",
            "task2Description",
            NEW,
            Optional.of(LocalDateTime.of(2022, 5, 3, 10, 0)),
            Optional.of(Duration.ofMinutes(60)));
    protected Epic epic1 = new Epic("Epic1Name", "Epic1Description", NEW);
    protected Epic epic2 = new Epic("Epic2Name", "Epic2Description", NEW);

    @Test
    public void addNewTask() {
        taskManager.addNewTask(task1);
        final int task1Id = task1.getId();
        final Task savedTask = taskManager.getTaskById(task1Id);
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверный идентификатор задачи");
    }

    @Test
    public void addNewEpic() {
        taskManager.addNewTask(epic1);
        final int epic1Id = epic1.getId();
        final Epic savedEpic1 = taskManager.getEpicById(epic1Id);
        assertNotNull(savedEpic1, "Задача не найдена");
        assertEquals(epic1, savedEpic1, "Задачи не совпадают");
        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают");
        assertEquals(1, savedEpic1.getId(), "Неверный идентификатор задачи");
    }

    @Test
    public void addNewSubTask() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        final int subTask1Epic1Id = subTask1Epic1.getId();
        final Subtask savedSubTask1Epic1 = taskManager.getSubtaskById(subTask1Epic1Id);
        assertNotNull(savedSubTask1Epic1, "Задача не найдена");
        assertEquals(subTask1Epic1, savedSubTask1Epic1, "Задачи не совпадают");
        final List<Subtask> subTasks = taskManager.getSubtasks();
        assertNotNull(subTasks, "Задачи не возвращаются");
        assertEquals(1, subTasks.size(), "Неверное количество задач");
        assertEquals(subTask1Epic1, subTasks.get(0), "Задачи не совпадают");
        assertEquals(2, savedSubTask1Epic1.getId(), "Неверный идентификатор задачи");
    }

    @Test
    public void updateTask() {
        taskManager.addNewTask(task1);
        final int task1Id = task1.getId();
        task1 = new Task("task1Name",
                "task1Description",
                Status.DONE,
                Optional.of(LocalDateTime.of(2022, 5, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        task1.setId(task1Id);
        taskManager.updateTask(task1);
        assertEquals(Status.DONE, task1.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void updateEpic() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.DONE,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        taskManager.updateEpic(epic1);
        assertEquals(Status.DONE, epic1.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void updateSubTask() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        final int subTask1Epic1Id = subTask1Epic1.getId();
        subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.DONE,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        subTask1Epic1.setId(subTask1Epic1Id);
        taskManager.updateSubtask(subTask1Epic1);
        assertEquals(Status.DONE, subTask1Epic1.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void deleteTaskById() {
        taskManager.addNewTask(task1);
        final int task1Id = task1.getId();
        taskManager.deleteTaskById(task1Id);
        taskManager.getTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задача не удалена");
    }

    @Test
    public void deleteEpicById() {
        taskManager.addNewTask(epic1);
        final int epic1Id = epic1.getId();
        taskManager.deleteEpicById(epic1Id);
        taskManager.getEpics();
        assertEquals(0, taskManager.getEpics().size(), "Задача не удалена");
    }

    @Test
    public void deleteSubTaskById() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        final int subTask1Epic1Id = subTask1Epic1.getId();
        taskManager.deleteSubtaskById(subTask1Epic1Id);
        taskManager.getSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Задача не удалена");
    }

    @Test
    public void deleteAllTasks() {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены");
    }

    @Test
    public void deleteAllEpic() {
        taskManager.addNewTask(epic1);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size(), "Задачи не удалены");
    }

    @Test
    public void deleteAllSubTasks() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        taskManager.deletesAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Задачи не удалены");
    }

    @Test
    public void getTaskById() {
        taskManager.addNewTask(task1);
        final int task1Id = task1.getId();
        final Task savedTask = taskManager.getTaskById(task1Id);
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
    }

    @Test
    public void getEpicById() {
        taskManager.addNewTask(epic1);
        final int epic1Id = epic1.getId();
        final Epic savedEpic = taskManager.getEpicById(epic1Id);
        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic1, savedEpic, "Задачи не совпадают");
    }

    @Test
    public void getSubTaskById() {
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        final int subTask1Epic1Id = subTask1Epic1.getId();
        final Subtask savedSubtask = taskManager.getSubtaskById(subTask1Epic1Id);
        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subTask1Epic1, savedSubtask, "Задачи не совпадают");
    }

    @Test
    public void ShouldReturn1IfTwoTasksInListOfTasks() {
        taskManager.addNewTask(task1);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void ShouldReturn0IfListOfTasksIsEmpty() {
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void getPrioritizedTasks() {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(epic1);
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        Subtask subTask2Epic1 = new Subtask(epic1.getId(),"subTask2Epic1Name",
                "subTask2Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 3, 10, 0)),
                Optional.of(Duration.ofMinutes(60)));
        taskManager.addNewTask(subTask2Epic1);
        Subtask subTask3Epic1 = new Subtask(epic1.getId(),"subTask3Epic1Name",
                "subTask3Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 5, 10, 0)),
                Optional.of(Duration.ofMinutes(10)));
        taskManager.addNewTask(subTask3Epic1);
        taskManager.addNewTask(epic2);
        assertEquals(5, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void getSubtasksOfEpic() {
        taskManager.addNewTask(epic1);
        final int epic1Id = epic1.getId();
        Subtask subTask1Epic1 = new Subtask(epic1.getId(),"subTask1Epic1Name",
                "subTask1Epic1Description",
                Status.NEW,
                Optional.of(LocalDateTime.of(2022, 7, 1, 10, 0)),
                Optional.of(Duration.ofMinutes(60 * 24)));
        taskManager.addNewTask(subTask1Epic1);
        final List<Integer> subtasks = taskManager.getSubtasksInEpic(epic1Id);
        assertEquals(1, subtasks.size(), "Неверное количество задач");
        assertEquals(subTask1Epic1, taskManager.getSubtaskById(subtasks.get(0)), "Задачи не совпадают");
    }

    @Test
    public void getHistory() {
        taskManager.addNewTask(task1);
        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().getHistoryViewTask().size());
    }
}