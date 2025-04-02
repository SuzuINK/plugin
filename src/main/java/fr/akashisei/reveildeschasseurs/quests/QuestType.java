package fr.akashisei.reveildeschasseurs.quests;

public enum QuestType {
    KILL("Tuer des monstres"),
    COLLECT("Collecter des items"),
    CONSUME("Consommer des items"),
    CRAFT("Craft d'items"),
    FISH("Pêcher des poissons"),
    TRADE("Échanger avec des PNJ"),
    DUNGEON("Compléter un donjon");

    private final String displayName;

    QuestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static QuestType fromString(String name) {
        try {
            return QuestType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
} 