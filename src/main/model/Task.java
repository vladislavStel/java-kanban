package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected int id;
    protected final String name;
    protected final String description;
    protected Status status;
    protected Optional<LocalDateTime> startTime;
    protected Optional<Duration> duration;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
    public static final Comparator<Task> comparator = (task1, task2) -> {
        if (task1.getId() == task2.getId()) {
            return 0;
        }
        if (task1.getStartTime().isEmpty()) {
            return -1;
        }
        if (task2.getStartTime().isEmpty()) {
            return 1;
        }
        return task1.getStartTime().get().compareTo(task2.getStartTime().get());
    };

    public Task(String name,
                String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id,
                String name,
                String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name,
                String description,
                Optional<LocalDateTime> startTime,
                Optional<Duration> duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name,
                String description,
                Status status,
                Optional<LocalDateTime> startTime,
                Optional<Duration> duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id,
                String name,
                String description,
                Status status,
                Optional<LocalDateTime> startTime,
                Optional<Duration> duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public Optional<Duration> getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return Type.TASK;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.of(startTime);
    }

    public void setDuration(Duration duration) {
        this.duration = Optional.of(duration);
    }

    public Optional<LocalDateTime> getEndTime() {
        return Optional.of(startTime.get().plus(duration.get()));
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getId(),
                getType(),
                getName(),
                getStatus(),
                getDescription(),
                getStartTime().isPresent() ? getStartTime().get().format(formatter) : "null",
                getDuration().isPresent() ? getDuration().get().toString() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }
}