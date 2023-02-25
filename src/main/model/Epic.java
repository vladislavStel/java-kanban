package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {
    private LocalDateTime endTime;
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name,
                String description,
                Status status) {
        super(name, description, status, Optional.empty(), Optional.empty());
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIds;
    }

    public void setSubtasksIdInEpic(ArrayList<Integer> subtasksIdInEpic) {
        this.subtasksIds = subtasksIdInEpic;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (endTime == null) {
            return Optional.empty();
        } else {
            return Optional.of(endTime);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void deleteSubtask(int id) {                     // удаляем id subtask из списка у epic
        if (!subtasksIds.isEmpty()) {
            subtasksIds.remove(Integer.valueOf(id));        // удаляем по значению
        }
    }

    public void deleteAllSubtasks() {
        subtasksIds.clear();
    }
}