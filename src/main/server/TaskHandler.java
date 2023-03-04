package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;
import model.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();
    private String jsonObject;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange h) {
        String method = h.getRequestMethod();
        URI uri = h.getRequestURI();
        String path = uri.getPath();
        Optional<String> query = Optional.ofNullable(uri.getQuery());
        InputStream body = h.getRequestBody();
        try {
            switch (method) {
                case "GET":
                    getRequestProcessing(h, query, path);
                    break;
                case "POST":
                    postRequestProcessing(h, body, path);
                    break;
                case "DELETE":
                    deleteRequestProcessing(h, query, path);
                    break;
                default:
                    System.out.println("Предусмотрены запросы GET, POST и DELETE, а получен " + method);
                    h.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void getRequestProcessing(HttpExchange h, Optional<String> query, String path) throws IOException {

        switch (path) {
            case "/tasks/":
                jsonObject = gson.toJson(taskManager.getPrioritizedTasks());
                sendJsonObject(h, jsonObject);
                break;
            case "/tasks/history/":
                jsonObject = gson.toJson(taskManager.getHistory());
                sendJsonObject(h, jsonObject);
                break;
            case "/tasks/task/":
                pathTasksTaskGetProcessing(h, query);
                break;
            case "/tasks/epic/":
                pathTasksEpicGetProcessing(h, query);
                break;
            case "/tasks/subtask/":
                pathTasksSubtaskGetProcessing(h, query);
                break;
            case "/tasks/subtask/epic/":
                pathTasksSubtaskEpicGetProcessing(h, query);
                break;
            default:
                System.out.println("Некорректный end-point " + path);
                h.sendResponseHeaders(404, 0);
        }
    }

    private void postRequestProcessing(HttpExchange h, InputStream body, String path) throws IOException {
        String bodyString = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(bodyString);
        if (!jsonElement.isJsonObject()) {
            System.out.println("Тело запроса клиента не соответствует формату JSON");
            h.sendResponseHeaders(405, 0);
            return;
        }
        switch (path) {
            case "/tasks/task/":
                pathTasksTaskPostProcessing(h, bodyString);
                break;
            case "/tasks/epic/":
                pathTasksEpicPostProcessing(h, bodyString);
                break;
            case "/tasks/subtask/":
                pathTasksSubtaskPostProcessing(h, bodyString);
                break;
            default:
                System.out.println("Некорректный end-point " + path);
                h.sendResponseHeaders(404, 0);
        }
    }

    private void deleteRequestProcessing(HttpExchange h, Optional<String> query, String path) throws IOException {

        switch (path) {
            case "/tasks/task/":
                pathTasksTaskDeleteProcessing(h, query);
                break;
            case "/tasks/epic/":
                pathTasksEpicDeleteProcessing(h, query);
                break;
            case "/tasks/subtask/":
                pathTasksSubtaskDeleteProcessing(h, query);
                break;
            default:
                System.out.println("Некорректный end-point " + path);
                h.sendResponseHeaders(404, 0);
        }
    }

    private void pathTasksTaskGetProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            jsonObject = gson.toJson(taskManager.getTasks());
            sendJsonObject(h, jsonObject);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Task task : taskManager.getTasks()) {
                    if (task.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    jsonObject = gson.toJson(taskManager.getTaskById(id));
                    sendJsonObject(h, jsonObject);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksEpicGetProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            jsonObject = gson.toJson(taskManager.getEpics());
            sendJsonObject(h, jsonObject);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Epic epic : taskManager.getEpics()) {
                    if (epic.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    jsonObject = gson.toJson(taskManager.getEpicById(id));
                    sendJsonObject(h, jsonObject);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksSubtaskGetProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            jsonObject = gson.toJson(taskManager.getSubtasks());
            sendJsonObject(h, jsonObject);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Subtask subtask : taskManager.getSubtasks()) {
                    if (subtask.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    jsonObject = gson.toJson(taskManager.getSubtaskById(id));
                    sendJsonObject(h, jsonObject);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksSubtaskEpicGetProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isPresent()) {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Epic epic : taskManager.getEpics()) {
                    if (epic.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists && !taskManager.getSubtasksInEpic(id).isEmpty()) {
                    jsonObject = gson.toJson(taskManager.getSubtasksInEpic(id));
                    sendJsonObject(h, jsonObject);
                } else if (idIsExists && taskManager.getSubtasksInEpic(id).isEmpty()) {
                    System.out.println("У эпика с id=" + id + " подзадачи отсутствуют");
                    h.sendResponseHeaders(405, 0);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksTaskPostProcessing(HttpExchange h, String bodyString) throws IOException {
        Task task = gson.fromJson(bodyString, Task.class);
        if (task.getId() == 0) {
            taskManager.addNewTask(task);
            System.out.println("Задача успешно добавлена");
            h.sendResponseHeaders(200, 0);
        } else {
            taskManager.updateTask(task);
            System.out.println("Задача успешно обновлена");
            h.sendResponseHeaders(200, 0);
        }
    }

    private void pathTasksEpicPostProcessing(HttpExchange h, String bodyString) throws IOException {
        Epic epic = gson.fromJson(bodyString, Epic.class);
        if (epic.getId() == 0) {
            taskManager.addNewTask(epic);
            System.out.println("Эпик успешно добавлен");
            h.sendResponseHeaders(200, 0);
        } else {
            taskManager.updateEpic(epic);
            System.out.println("Эпик успешно обновлен");
            h.sendResponseHeaders(200, 0);
        }
    }

    private void pathTasksSubtaskPostProcessing(HttpExchange h, String bodyString) throws IOException {
        Subtask subtask = gson.fromJson(bodyString, Subtask.class);
        if (subtask.getId() == 0) {
            taskManager.addNewTask(subtask);
            System.out.println("Подзадача успешно добавлена");
            h.sendResponseHeaders(200, 0);
        } else {
            taskManager.updateSubtask(subtask);
            System.out.println("Подзадача успешно обновлена");
            h.sendResponseHeaders(200, 0);
        }
    }

    private void pathTasksTaskDeleteProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            taskManager.deleteAllTasks();
            System.out.println("Все задачи удалены");
            h.sendResponseHeaders(200, 0);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Task task : taskManager.getTasks()) {
                    if (task.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    taskManager.deleteTaskById(id);
                    System.out.println("Задача с id=" + id + " удалена");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Задачи с id=" + id + " не существует");
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksEpicDeleteProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            taskManager.deleteAllEpics();
            System.out.println("Все эпики удалены");
            h.sendResponseHeaders(200, 0);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Epic epic : taskManager.getEpics()) {
                    if (epic.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    taskManager.deleteEpicById(id);
                    System.out.println("Эпик с id=" + id + " удален");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void pathTasksSubtaskDeleteProcessing(HttpExchange h, Optional<String> query) throws IOException {
        if (query.isEmpty()) {
            taskManager.deletesAllSubtasks();
            System.out.println("Все подзадачи удалены");
            h.sendResponseHeaders(200, 0);
        } else {
            String[] queryElements = query.get().split("=");
            if (queryElements[0].equals("id") && isNumeric(queryElements[1])) {
                int id = Integer.parseInt(queryElements[1]);
                boolean idIsExists = false;
                for (Subtask subtask : taskManager.getSubtasks()) {
                    if (subtask.getId() == id) {
                        idIsExists = true;
                        break;
                    }
                }
                if (idIsExists) {
                    taskManager.deleteSubtaskById(id);
                    System.out.println("Подзадача с id=" + id + " удалена");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Некорректный идентификатор id=" + id);
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Некорректные параметры строки запроса " + query.get());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void sendJsonObject(HttpExchange h, String jsonObject) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, 0);
        try (OutputStream os = h.getResponseBody()) {
            os.write(jsonObject.getBytes());
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}