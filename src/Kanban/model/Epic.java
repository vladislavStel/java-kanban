package Kanban.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdInEpic = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIdInEpic;
    }

    public void setSubtasksIdInEpic(ArrayList<Integer> subtasksIdInEpic) {
        this.subtasksIdInEpic = subtasksIdInEpic;
    }

    @Override
    public TypesTasks getType() {
        return TypesTasks.EPIC;
    }

    public void deleteSubtask(int id) {
        if (!subtasksIdInEpic.isEmpty()) {
            subtasksIdInEpic.remove(Integer.valueOf(id));
        }
    }

    public void deleteAllSubtasks() {
        if (!subtasksIdInEpic.isEmpty()) {
            subtasksIdInEpic.clear();
        }
    }
}