package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void start() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}