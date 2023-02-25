package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @BeforeEach
    public void start() {
        taskManager = Managers.getDefault();
    }
}