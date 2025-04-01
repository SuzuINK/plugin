package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public void loadDungeon(Dungeon dungeon) {
        String path = "dungeons." + dungeon.getId();
        dungeon.setName(config.getString(path + ".name", "Donjon"));
        dungeon.setDescription(config.getString(path + ".description", "Description du donjon"));
        dungeon.setMinLevel(Math.toIntExact(config.getLong(path + ".min_level", 1)));
        dungeon.setMaxPlayers(Math.toIntExact(config.getLong(path + ".max_players", 4)));
        dungeon.setBaseRewardMoney(Math.toIntExact(config.getLong(path + ".base_reward_money", 100)));
        dungeon.setBaseRewardExp(Math.toIntExact(config.getLong(path + ".base_reward_exp", 50)));
        dungeon.setTimeLimit(config.getLong(path + ".time_limit", 3600));
        dungeon.setDifficultyMultiplier(config.getDouble(path + ".difficulty_multiplier", 1.0));
        dungeon.setTags(config.getStringList(path + ".tags"));
        dungeon.setRequiredQuests(config.getStringList(path + ".required_quests"));
        dungeon.setRequiredItems(config.getStringList(path + ".required_items"));
        dungeon.setForbiddenItems(config.getStringList(path + ".forbidden_items"));
        dungeon.setAllowedClasses(config.getStringList(path + ".allowed_classes"));
        dungeon.setAllowedRanks(config.getStringList(path + ".allowed_ranks"));
    }

    // Méthodes pour les quêtes
    public void saveQuest(Quest quest) {
        File file = new File(questsFolder, quest.getId() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", quest.getName());
        config.set("description", quest.getDescription());
        config.set("type", quest.getType());
        config.set("required_amount", quest.getRequiredAmount());
        config.set("target", quest.getTarget());
        config.set("reward_weapon", quest.getRewardWeapon());
        config.set("tier", quest.getTier());
        config.set("required_level", quest.getRequiredLevel());
        config.set("required_items", quest.getRequiredItems());
        config.set("rewards", quest.getRewards());
        config.set("repeatable", quest.isRepeatable());
        config.set("max_completions", quest.getMaxCompletions());
        config.set("reward_exp", quest.getRewardExp());
        config.set("reward_money", quest.getRewardMoney());
        config.set("required_quests", new ArrayList<>(quest.getRequiredQuests()));
        config.set("allowed_classes", new ArrayList<>(quest.getAllowedClasses()));
        config.set("allowed_ranks", new ArrayList<>(quest.getAllowedRanks()));
        config.set("cooldown", quest.getCooldown());
        config.set("objectives", quest.getObjectives());

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
            config.getString("type", "KILL"),
            config.getInt("required_amount", 1),
            config.getString("target", "ZOMBIE"),
            config.getString("reward_weapon", ""),
            config.getString("tier", "COMMON"),
            config.getInt("required_level", 1),
            new HashMap<>(), // requiredItems
            config.getStringList("rewards"),
            config.getBoolean("repeatable", false),
            config.getInt("max_completions", 1),
            config.getInt("reward_exp", 0),
            config.getInt("reward_money", 0),
            new HashSet<>(config.getStringList("required_quests")),
            new HashSet<>(config.getStringList("allowed_classes")),
            new HashSet<>(config.getStringList("allowed_ranks")),
            config.getLong("cooldown", 0)
        );

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
        data.setLevel((int) config.getLong("level", 1));
        data.setExperience((int) config.getLong("experience", 0));
        data.setMoney(config.getLong("money", 0));
        data.setMobsKilled((int) config.getLong("mobs_killed", 0));
        data.setDeaths((int) config.getLong("deaths", 0));

        // Charger les quêtes actives
        data.setActiveQuests(config.getStringList("active_quests"));
        
        // Charger les progrès des quêtes
        ConfigurationSection questProgressSection = config.getConfigurationSection("quest_progress");
        if (questProgressSection != null) {
            for (String questId : questProgressSection.getKeys(false)) {
                data.setQuestProgress(questId, questProgressSection.getInt(questId));
            }
        }

        // Charger les complétions de quêtes
        ConfigurationSection questCompletionsSection = config.getConfigurationSection("quest_completions");
        if (questCompletionsSection != null) {
            for (String questId : questCompletionsSection.getKeys(false)) {
                int completions = questCompletionsSection.getInt(questId);
                for (int i = 0; i < completions; i++) {
                    data.incrementQuestCompletions(questId);
                }
            }
        }

        return data;
    }

    public void incrementQuestCompletion(PlayerData data, String questId) {
        data.incrementQuestCompletions(questId);
        savePlayerData(data);
    }

    public void savePlayerData(PlayerData data) {
        File file = new File(playersFolder, data.getUuid().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", data.getName());
        config.set("class", data.getPlayerClass());
        config.set("rank", data.getRank());
        config.set("level", data.getLevel());
        config.set("experience", data.getExperience());
        config.set("money", data.getMoney());
        config.set("mobs_killed", data.getMobsKilled());
        config.set("deaths", data.getDeaths());
        config.set("active_quests", data.getActiveQuests());
        
        // Sauvegarder les progrès des quêtes
        ConfigurationSection questProgressSection = config.createSection("quest_progress");
        for (Map.Entry<String, Integer> entry : data.getQuestProgress().entrySet()) {
            questProgressSection.set(entry.getKey(), entry.getValue());
        }
        
        // Sauvegarder les complétions de quêtes
        ConfigurationSection questCompletionsSection = config.createSection("quest_completions");
        for (Map.Entry<String, Integer> entry : data.getQuestCompletions().entrySet()) {
            questCompletionsSection.set(entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les données du joueur " + data.getName());
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