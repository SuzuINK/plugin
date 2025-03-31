package fr.akashisei.reveildeschasseurs;

import fr.akashisei.reveildeschasseurs.commands.DungeonCommand;
import fr.akashisei.reveildeschasseurs.commands.ProfileCommand;
import fr.akashisei.reveildeschasseurs.commands.QuestCommand;
import fr.akashisei.reveildeschasseurs.commands.ClassCommand;
import fr.akashisei.reveildeschasseurs.commands.RankCommand;
import fr.akashisei.reveildeschasseurs.listeners.DungeonListener;
import fr.akashisei.reveildeschasseurs.listeners.MenuListener;
import fr.akashisei.reveildeschasseurs.listeners.QuestListener;
import fr.akashisei.reveildeschasseurs.listeners.PlayerListener;
import fr.akashisei.reveildeschasseurs.listeners.MobListener;
import fr.akashisei.reveildeschasseurs.managers.ConfigManager;
import fr.akashisei.reveildeschasseurs.managers.DungeonManager;
import fr.akashisei.reveildeschasseurs.managers.MenuManager;
import fr.akashisei.reveildeschasseurs.managers.PlayerDataManager;
import fr.akashisei.reveildeschasseurs.managers.QuestManager;
import fr.akashisei.reveildeschasseurs.generators.DungeonBuilder;
import fr.akashisei.reveildeschasseurs.managers.MobManager;
import fr.akashisei.reveildeschasseurs.managers.CustomItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ReveilDesChasseurs extends JavaPlugin {
    private static ReveilDesChasseurs instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private QuestManager questManager;
    private DungeonManager dungeonManager;
    private DungeonBuilder dungeonBuilder;
    private MobManager mobManager;
    private MenuManager menuManager;
    private DungeonListener dungeonListener;
    private QuestListener questListener;
    private MenuListener menuListener;
    private CustomItemManager customItemManager;

    @Override
    public void onEnable() {
        instance = this;

        // Sauvegarder la configuration par défaut
        saveDefaultConfig();

        // Initialiser les gestionnaires
        this.configManager = new ConfigManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.questManager = new QuestManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.dungeonBuilder = new DungeonBuilder(this);
        this.mobManager = new MobManager(this);
        this.menuManager = new MenuManager(this);
        this.customItemManager = new CustomItemManager(this);

        // Initialiser les listeners
        this.dungeonListener = new DungeonListener(this);
        this.questListener = new QuestListener(this);
        this.menuListener = new MenuListener(this);

        // Charger les données
        this.configManager.loadConfig();
        this.playerDataManager.loadAllPlayerData();
        this.questManager.loadQuests();
        this.dungeonManager.loadDungeons();
        this.customItemManager.loadCustomItems();

        // Enregistrer les listeners
        getServer().getPluginManager().registerEvents(dungeonListener, this);
        getServer().getPluginManager().registerEvents(questListener, this);
        getServer().getPluginManager().registerEvents(menuListener, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);

        // Enregistrer les commandes
        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("quest").setExecutor(new QuestCommand(this));
        getCommand("profile").setExecutor(new ProfileCommand(this));
        getCommand("class").setExecutor(new ClassCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));

        getLogger().info("ReveilDesChasseurs a été activé avec succès!");
    }

    @Override
    public void onDisable() {
        // Sauvegarder toutes les données
        if (playerDataManager != null) {
            playerDataManager.saveAllPlayerData();
        }
        if (questManager != null) {
            questManager.saveQuests();
        }
        if (dungeonManager != null) {
            dungeonManager.saveDungeons();
        }
        if (configManager != null) {
            configManager.saveConfig();
        }
        if (customItemManager != null) {
            customItemManager.saveCustomItems();
        }

        getLogger().info("ReveilDesChasseurs a été désactivé avec succès!");
    }

    public static ReveilDesChasseurs getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public DungeonBuilder getDungeonBuilder() {
        return dungeonBuilder;
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }
} 