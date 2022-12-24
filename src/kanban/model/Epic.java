package kanban.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
    public Epic(String name, String description, int id) {
        super(id, name, description);
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIds;
    }

    public void setSubtasksIdInEpic(ArrayList<Integer> subtasksIdInEpic) {
        this.subtasksIds = subtasksIdInEpic;
    }

    @Override
    public Types getType() {
        return Types.EPIC;
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