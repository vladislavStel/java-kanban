package Kanban.model;

import java.util.Objects;

public class Task {
    protected final String name;
    protected final String description;
    protected TasksStatus status;
    protected int id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TasksStatus.NEW;
    }

    public Task(String name, String description, TasksStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TasksStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TasksStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public TypesTasks getType() {
        return TypesTasks.TASK;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TasksStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status && id == task.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }
}