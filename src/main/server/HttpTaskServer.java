package server;

import com.sun.net.httpserver.HttpServer;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("TaskServer запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("TaskServer остановлен на порту " + PORT);
    }
}