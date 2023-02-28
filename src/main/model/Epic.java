package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private final List<Subtask> subtasksInEpic = new ArrayList<>();

    public Epic(String name,
                String description,
                Status status) {
        super(name, description, status, Optional.empty(), Optional.empty());
    }

    public List<Subtask> getSubtasksInEpic() {
        return new ArrayList<>(subtasksInEpic);
    }

    public void setSubtasksInEpic(Subtask subtask) {
        subtasksInEpic.add(subtask);
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public Status getStatus() {
        int statusNewCounter = 0;
        int statusDoneCounter = 0;
        if (getSubtasksInEpic().isEmpty()) {
            return Status.NEW;
        } else {
            for (Subtask subtask : getSubtasksInEpic()) {
                if (subtask.getStatus() == Status.NEW) {
                    statusNewCounter++;
                } else if (subtask.getStatus() == Status.DONE) {
                    statusDoneCounter++;
                }
            }
            if (statusNewCounter == getSubtasksInEpic().size()) {
                return Status.NEW;
            } else if (statusDoneCounter == getSubtasksInEpic().size()) {
                return Status.DONE;
            } else {
                return Status.IN_PROGRESS;
            }
        }
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        if (subtasksInEpic.isEmpty()) {
            return Optional.empty();
        }
        Optional<LocalDateTime> startTime = subtasksInEpic.get(0).getStartTime();
        for (Subtask subtask : getSubtasksInEpic()) {
            if (subtask.getStartTime().get().isBefore(startTime.get())) {
                startTime = subtask.getStartTime();
            }
        }
        return startTime;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (subtasksInEpic.isEmpty()) {
            return Optional.empty();
        }
        Optional<LocalDateTime> endTime = subtasksInEpic.get(0).getStartTime();
        for (Subtask subtask : getSubtasksInEpic()) {
            if (subtask.getEndTime().get().isAfter(endTime.get())) {
                endTime = subtask.getEndTime();
            }
        }
        return endTime;
    }

    @Override
    public Optional<Duration> getDuration() {
        if (subtasksInEpic.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Duration.between(getEndTime().get(), getStartTime().get()));
    }

    public void deleteSubtask(Subtask subtask) {
        if (!subtasksInEpic.isEmpty()) {
            subtasksInEpic.remove(subtask);
        }
    }

    public void deleteAllSubtasks() {
        if (!subtasksInEpic.isEmpty()) {
            subtasksInEpic.clear();
        }
    }
}