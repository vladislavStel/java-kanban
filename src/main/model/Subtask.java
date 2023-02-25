package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int epicId,
                   String name,
                   String description,
                   Status status,
                   Optional<LocalDateTime> startTime,
                   Optional<Duration> duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getIdEpic() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return super.toString() + "," +getIdEpic();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}