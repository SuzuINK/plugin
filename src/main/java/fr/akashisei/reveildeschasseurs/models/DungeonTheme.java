package fr.akashisei.reveildeschasseurs.models;

public enum DungeonTheme {
    MINE("Mine abandonnée", "Une mine sombre et dangereuse"),
    CRYPT("Crypte", "Une crypte ancienne remplie de morts-vivants"),
    CASTLE("Château", "Un château en ruines envahi par les monstres"),
    CAVE("Grotte", "Une grotte naturelle abritant des créatures hostiles"),
    TEMPLE("Temple", "Un temple ancien aux pièges mortels");

    private final String name;
    private final String description;

    DungeonTheme(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
} 