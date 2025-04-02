package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.models.Quest;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder la configuration : " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public String getMessage(String path) {
        String message = config.getString(path);
        if (message == null) {
            return ChatColor.RED + "Message non trouvé : " + path;
        }
        String prefix = config.getString("messages.prefix", "§6[ReveilDesChasseurs] §r");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public String getMessageWithoutPrefix(String path) {
        String message = config.getString(path);
        if (message == null) {
            return ChatColor.RED + "Message non trouvé : " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
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
        config.set("required", quest.getRequired());
        config.set("reward", quest.getReward());
        config.set("requirements", quest.getRequirements());
        config.set("rewards", quest.getRewards());
        config.set("required_level", quest.getRequiredLevel());
        config.set("category", quest.getCategory());
        config.set("difficulty", quest.getDifficulty());
        config.set("objectives", quest.getObjectives());
        config.set("repeatable", quest.isRepeatable());
        config.set("cooldown", quest.getCooldown());
        config.set("min_level", quest.getMinLevel());
        config.set("max_level", quest.getMaxLevel());
        config.set("experience_reward", quest.getExperienceReward());
        config.set("required_quests", new ArrayList<>(quest.getRequiredQuests()));
        config.set("allowed_classes", new ArrayList<>(quest.getAllowedClasses()));
        config.set("allowed_ranks", new ArrayList<>(quest.getAllowedRanks()));
        config.set("quest_cooldown", quest.getQuestCooldown());

        // Sauvegarder les items requis
        for (Map.Entry<ItemStack, Integer> entry : quest.getRequiredItems().entrySet()) {
            String itemId = plugin.getCustomItemManager().getCustomItemId(entry.getKey());
            if (itemId != null) {
                config.set("required_items." + itemId, entry.getValue());
            }
        }

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
        
        // Charger les items requis
        HashMap<ItemStack, Integer> requiredItems = new HashMap<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("required_items");
        if (itemsSection != null) {
            for (String itemId : itemsSection.getKeys(false)) {
                ItemStack item = plugin.getCustomItemManager().getCustomItem(itemId);
                if (item != null) {
                    requiredItems.put(item, itemsSection.getInt(itemId));
                }
            }
        }

        return new Quest(
            id,
            config.getString("name", "Quête"),
            config.getString("description", "Une quête mystérieuse"),
            config.getString("type", "KILL"),
            config.getInt("required_amount", 1),
            config.getInt("reward_money", 100),
            config.getStringList("requirements"),
            config.getStringList("rewards"),
            config.getInt("required_level", 1),
            config.getString("category", "MAIN"),
            config.getString("difficulty", "NORMAL"),
            requiredItems,
            config.getStringList("objectives"),
            config.getBoolean("repeatable", false),
            config.getInt("cooldown", 0),
            config.getInt("min_level", 1),
            config.getInt("max_level", 100),
            config.getInt("reward_exp", 0),
            new HashSet<>(config.getStringList("required_quests")),
            new HashSet<>(config.getStringList("allowed_classes")),
            new HashSet<>(config.getStringList("allowed_ranks")),
            config.getLong("quest_cooldown", 0)
        );
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
            return new PlayerData(playerId, "Unknown");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(playerId, config.getString("name", "Unknown"));
        
        data.setPlayerClass(config.getString("class", "NONE"));
        data.setRank(config.getString("rank", "NONE"));
        data.setLevel(config.getInt("level", 1));
        data.setExperience(config.getInt("experience", 0));
        data.setMoney(config.getLong("money", 0));
        data.setActiveQuests(new HashSet<>(config.getStringList("active_quests")));
        data.setCompletedQuests(new HashSet<>(config.getStringList("completed_quests")));
        
        // Charger la progression des quêtes
        Map<String, Integer> questProgress = new HashMap<>();
        ConfigurationSection progressSection = config.getConfigurationSection("quest_progress");
        if (progressSection != null) {
            for (String questId : progressSection.getKeys(false)) {
                questProgress.put(questId, progressSection.getInt(questId));
            }
        }
        data.setQuestProgress(questProgress);
        
        // Charger les cooldowns des quêtes
        Map<String, Long> questCooldowns = new HashMap<>();
        ConfigurationSection cooldownSection = config.getConfigurationSection("quest_cooldowns");
        if (cooldownSection != null) {
            for (String questId : cooldownSection.getKeys(false)) {
                questCooldowns.put(questId, cooldownSection.getLong(questId));
            }
        }
        data.setQuestCooldowns(questCooldowns);
        
        // Charger les statistiques
        Map<String, Integer> statistics = new HashMap<>();
        ConfigurationSection statsSection = config.getConfigurationSection("statistics");
        if (statsSection != null) {
            for (String stat : statsSection.getKeys(false)) {
                statistics.put(stat, statsSection.getInt(stat));
            }
        }
        data.setStatistics(statistics);

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
        config.set("active_quests", new ArrayList<>(data.getActiveQuests()));
        config.set("completed_quests", new ArrayList<>(data.getCompletedQuests()));
        
        // Sauvegarder la progression des quêtes
        for (Map.Entry<String, Integer> entry : data.getQuestProgress().entrySet()) {
            config.set("quest_progress." + entry.getKey(), entry.getValue());
        }
        
        // Sauvegarder les cooldowns des quêtes
        for (Map.Entry<String, Long> entry : data.getQuestCooldowns().entrySet()) {
            config.set("quest_cooldowns." + entry.getKey(), entry.getValue());
        }
        
        // Sauvegarder les statistiques
        for (Map.Entry<String, Integer> entry : data.getStatistics().entrySet()) {
            config.set("statistics." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les données du joueur " + data.getPlayerName());
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