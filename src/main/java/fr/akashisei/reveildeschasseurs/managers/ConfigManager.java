package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.models.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {
    private final ReveilDesChasseurs plugin;
    private FileConfiguration config;
    private File configFile;
    private final File dungeonsFolder;
    private final File questsFolder;
    private final File playersFolder;

    public ConfigManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        loadConfig();
        this.dungeonsFolder = new File(plugin.getDataFolder(), "dungeons");
        this.questsFolder = new File(plugin.getDataFolder(), "quests");
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        
        if (!dungeonsFolder.exists()) {
            dungeonsFolder.mkdirs();
        }
        if (!questsFolder.exists()) {
            questsFolder.mkdirs();
        }
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // Méthodes pour les donjons
    public void saveDungeon(Dungeon dungeon) {
        File file = new File(dungeonsFolder, dungeon.getId() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", dungeon.getName());
        config.set("description", dungeon.getDescription());
        config.set("min_level", dungeon.getMinLevel());
        config.set("max_players", dungeon.getMaxPlayers());
        config.set("time_limit", dungeon.getTimeLimit());
        config.set("difficulty_multiplier", dungeon.getDifficultyMultiplier());
        config.set("base_reward_exp", dungeon.getBaseRewardExp());
        config.set("base_reward_money", dungeon.getBaseRewardMoney());
        config.set("tags", dungeon.getTags());
        config.set("required_quests", dungeon.getRequiredQuests());
        config.set("required_items", dungeon.getRequiredItems());
        config.set("forbidden_items", dungeon.getForbiddenItems());
        config.set("allowed_classes", dungeon.getAllowedClasses());
        config.set("allowed_ranks", dungeon.getAllowedRanks());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder le donjon " + dungeon.getName());
            e.printStackTrace();
        }
    }

    public Dungeon loadDungeon(String id) {
        File file = new File(dungeonsFolder, id + ".yml");
        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Dungeon dungeon = new Dungeon(
            id,
            config.getString("name", "Donjon"),
            config.getString("description", "Un donjon mystérieux"),
            config.getInt("min_level", 1),
            DungeonTheme.valueOf(config.getString("theme", "MEDIEVAL"))
        );
        
        dungeon.setMaxPlayers(config.getInt("max_players", 4));
        dungeon.setTimeLimit(config.getLong("time_limit", 3600));
        dungeon.setDifficultyMultiplier(config.getDouble("difficulty_multiplier", 1.0));
        dungeon.setBaseRewardExp(config.getLong("base_reward_exp", 1000));
        dungeon.setBaseRewardMoney(config.getLong("base_reward_money", 100));

        dungeon.setTags(config.getStringList("tags"));
        dungeon.setRequiredQuests(config.getStringList("required_quests"));
        dungeon.setRequiredItems(config.getStringList("required_items"));
        dungeon.setForbiddenItems(config.getStringList("forbidden_items"));
        dungeon.setAllowedClasses(config.getStringList("allowed_classes"));
        dungeon.setAllowedRanks(config.getStringList("allowed_ranks"));

        return dungeon;
    }

    // Méthodes pour les quêtes
    public void saveQuest(Quest quest) {
        File file = new File(questsFolder, quest.getId() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", quest.getName());
        config.set("description", quest.getDescription());
        config.set("min_level", quest.getMinLevel());
        config.set("reward_exp", quest.getRewardExp());
        config.set("reward_money", quest.getRewardMoney());
        config.set("repeatable", quest.isRepeatable());
        config.set("cooldown", quest.getCooldown());
        config.set("max_completions", quest.getMaxCompletions());
        config.set("required_quests", quest.getRequiredQuests());
        config.set("required_items", quest.getRequiredItems());
        config.set("objectives", quest.getObjectives());
        config.set("allowed_classes", quest.getAllowedClasses());
        config.set("allowed_ranks", quest.getAllowedRanks());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder la quête " + quest.getName());
            e.printStackTrace();
        }
    }

    public Quest loadQuest(String id) {
        File file = new File(questsFolder, id + ".yml");
        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Quest quest = new Quest(
            id,
            config.getString("name", "Quête"),
            config.getString("description", "Une quête mystérieuse"),
            config.getInt("min_level", 1)
        );
        
        quest.setRewardExp(config.getLong("reward_exp", 500));
        quest.setRewardMoney(config.getLong("reward_money", 50));
        quest.setRepeatable(config.getBoolean("repeatable", false));
        quest.setCooldown(config.getLong("cooldown", 0));
        quest.setMaxCompletions(config.getInt("max_completions", 1));

        quest.setRequiredQuests(config.getStringList("required_quests"));
        quest.setRequiredItems(config.getStringList("required_items"));
        quest.setObjectives(config.getStringList("objectives"));
        quest.setAllowedClasses(config.getStringList("allowed_classes"));
        quest.setAllowedRanks(config.getStringList("allowed_ranks"));

        return quest;
    }

    // Méthodes utilitaires
    public List<String> getDungeonIds() {
        List<String> ids = new ArrayList<>();
        File[] files = dungeonsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                ids.add(file.getName().replace(".yml", ""));
            }
        }
        return ids;
    }

    public List<String> getQuestIds() {
        List<String> ids = new ArrayList<>();
        File[] files = questsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                ids.add(file.getName().replace(".yml", ""));
            }
        }
        return ids;
    }

    public Map<String, Object> getDungeonSettings(String dungeonId) {
        Map<String, Object> settings = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("dungeons." + dungeonId);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                settings.put(key, section.get(key));
            }
        }
        return settings;
    }

    public Map<String, Object> getQuestSettings(String questId) {
        Map<String, Object> settings = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("quests." + questId);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                settings.put(key, section.get(key));
            }
        }
        return settings;
    }

    // Méthodes pour les joueurs
    public List<String> getPlayerIds() {
        List<String> ids = new ArrayList<>();
        File[] files = playersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                ids.add(file.getName().replace(".yml", ""));
            }
        }
        return ids;
    }

    public PlayerData loadPlayerData(UUID playerId) {
        File file = new File(playersFolder, playerId.toString() + ".yml");
        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(playerId, config.getString("name", "Unknown"));
        
        data.setPlayerClass(config.getString("class", "Aventurier"));
        data.setRank(config.getString("rank", "Novice"));
        data.setLevel(config.getInt("level", 1));
        data.setExperience(config.getLong("experience", 0));
        data.setMoney(config.getLong("money", 0));
        data.setMobsKilled(config.getInt("mobs_killed", 0));
        data.setDeaths(config.getInt("deaths", 0));

        // Charger les quêtes actives
        data.setActiveQuests(config.getStringList("active_quests"));

        // Charger la progression des quêtes
        if (config.contains("quest_progress")) {
            ConfigurationSection progressSection = config.getConfigurationSection("quest_progress");
            if (progressSection != null) {
                for (String questId : progressSection.getKeys(false)) {
                    data.setQuestProgress(questId, progressSection.getInt(questId));
                }
            }
        }

        // Charger les cooldowns des quêtes
        if (config.contains("quest_cooldowns")) {
            ConfigurationSection cooldownSection = config.getConfigurationSection("quest_cooldowns");
            if (cooldownSection != null) {
                for (String questId : cooldownSection.getKeys(false)) {
                    data.setQuestCooldown(questId, cooldownSection.getLong(questId));
                }
            }
        }

        // Charger les complétions des quêtes
        if (config.contains("quest_completions")) {
            ConfigurationSection completionSection = config.getConfigurationSection("quest_completions");
            if (completionSection != null) {
                for (String questId : completionSection.getKeys(false)) {
                    data.setQuestCompletions(questId, completionSection.getInt(questId));
                }
            }
        }

        return data;
    }

    public void savePlayerData(PlayerData data) {
        File file = new File(playersFolder, data.getPlayerId().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", data.getPlayerName());
        config.set("class", data.getPlayerClass());
        config.set("rank", data.getRank());
        config.set("level", data.getLevel());
        config.set("experience", data.getExperience());
        config.set("money", data.getMoney());
        config.set("mobs_killed", data.getMobsKilled());
        config.set("deaths", data.getDeaths());
        config.set("active_quests", data.getActiveQuests());

        // Sauvegarder la progression des quêtes
        for (Map.Entry<String, Integer> entry : data.getQuestProgress().entrySet()) {
            config.set("quest_progress." + entry.getKey(), entry.getValue());
        }

        // Sauvegarder les cooldowns des quêtes
        for (Map.Entry<String, Long> entry : data.getQuestCooldowns().entrySet()) {
            config.set("quest_cooldowns." + entry.getKey(), entry.getValue());
        }

        // Sauvegarder les complétions des quêtes
        for (Map.Entry<String, Integer> entry : data.getQuestCompletions().entrySet()) {
            config.set("quest_completions." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les données du joueur " + data.getPlayerId());
            e.printStackTrace();
        }
    }

    public void deletePlayerData(UUID playerId) {
        File file = new File(playersFolder, playerId.toString() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
} 