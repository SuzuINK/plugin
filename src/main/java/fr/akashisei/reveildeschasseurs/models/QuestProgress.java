package fr.akashisei.reveildeschasseurs.models;

import java.util.HashMap;
import java.util.Map;
import fr.akashisei.reveildeschasseurs.quests.Quest;

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
        Map<String, Integer> objectives = quest.getObjectives();
        for (Map.Entry<String, Integer> entry : objectives.entrySet()) {
            String objective = entry.getKey();
            int required = entry.getValue();
            int current = getObjectiveProgress(objective);
            
            if (current < required) {
                return false;
            }
        }
        return true;
    }

    public double getCompletionPercentage(Quest quest) {
        Map<String, Integer> objectives = quest.getObjectives();
        if (objectives.isEmpty()) {
            return 0.0;
        }

        int totalObjectives = objectives.size();
        int completedObjectives = 0;

        for (Map.Entry<String, Integer> entry : objectives.entrySet()) {
            String objective = entry.getKey();
            int required = entry.getValue();
            int current = getObjectiveProgress(objective);
            
            if (current >= required) {
                completedObjectives++;
            }
        }

        return (double) completedObjectives / totalObjectives * 100.0;
    }
} 