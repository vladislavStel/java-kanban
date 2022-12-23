package Kanban.model;

import java.util.Objects;

public class Subtask extends Task {
    private final int idEpic;

    public Subtask(String name, String description, TasksStatus status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public TypesTasks getType() {
        return TypesTasks.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" + "id=" + super.getId() +
                ", idEpic=" + idEpic +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }
}