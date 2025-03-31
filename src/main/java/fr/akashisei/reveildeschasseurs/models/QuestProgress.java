package fr.akashisei.reveildeschasseurs.models;

import java.util.HashMap;
import java.util.Map;

public class QuestProgress {
    private final String questId;
    private final Map<String, Integer> progress;

    public QuestProgress(String questId) {
        this.questId = questId;
        this.progress = new HashMap<>();
    }

    public String getQuestId() {
        return questId;
    }

    public Map<String, Integer> getProgress() {
        return progress;
    }

    public void updateProgress(String objective, int amount) {
        int currentProgress = progress.getOrDefault(objective, 0);
        progress.put(objective, currentProgress + amount);
    }

    public int getObjectiveProgress(String objective) {
        return progress.getOrDefault(objective, 0);
    }

    public boolean isCompleted(Quest quest) {
        for (String objective : quest.getObjectives()) {
            String[] parts = objective.split(":");
            if (parts.length != 2) continue;
            
            int required = Integer.parseInt(parts[1]);
            int current = getObjectiveProgress(parts[0]);
            
            if (current < required) {
                return false;
            }
        }
        return true;
    }

    public double getCompletionPercentage(Quest quest) {
        if (quest.getObjectives().isEmpty()) {
            return 0.0;
        }

        int totalObjectives = quest.getObjectives().size();
        int completedObjectives = 0;

        for (String objective : quest.getObjectives()) {
            String[] parts = objective.split(":");
            if (parts.length != 2) continue;
            
            int required = Integer.parseInt(parts[1]);
            int current = getObjectiveProgress(parts[0]);
            
            if (current >= required) {
                completedObjectives++;
            }
        }

        return (double) completedObjectives / totalObjectives * 100.0;
    }
} 