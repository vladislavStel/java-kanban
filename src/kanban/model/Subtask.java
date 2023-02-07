package kanban.model;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int idEpic, String name, String description) {
        super(name, description);
        this.epicId = idEpic;
    }

    public Subtask(int idEpic, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = idEpic;
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