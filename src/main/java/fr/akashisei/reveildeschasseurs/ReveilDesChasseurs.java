package fr.akashisei.reveildeschasseurs;

import fr.akashisei.reveildeschasseurs.commands.*;
import fr.akashisei.reveildeschasseurs.listeners.*;
import fr.akashisei.reveildeschasseurs.managers.*;
import fr.akashisei.reveildeschasseurs.generators.dungeon.DungeonGenerator;
import fr.akashisei.reveildeschasseurs.generators.dungeon.DungeonBuilder;
import fr.akashisei.reveildeschasseurs.mobs.*;
import fr.akashisei.reveildeschasseurs.dungeon.*;
import fr.akashisei.reveildeschasseurs.utils.*;
import fr.akashisei.reveildeschasseurs.items.CustomItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ReveilDesChasseurs extends JavaPlugin {
    private static ReveilDesChasseurs instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private QuestManager questManager;
    private DungeonManager dungeonManager;
    private DungeonGenerator dungeonGenerator;
    private DungeonBuilder dungeonBuilder;
    private MobManager mobManager;
    private MenuManager menuManager;
    private DungeonListener dungeonListener;
    private QuestListener questListener;
    private MenuListener menuListener;
    private CustomItemManager customItemManager;
    private EconomyManager economyManager;
    private PlayerRankManager rankManager;
    private MobEffectManager mobEffectManager;
    private DungeonMobManager dungeonMobManager;
    private SkinInstaller skinInstaller;
    private WeaponManager weaponManager;
    private PlayerListener playerListener;
    private InventoryManager inventoryManager;
    private HelpManager helpManager;

    @Override
    public void onEnable() {
        instance = this;

        // Sauvegarder la configuration par défaut
        saveDefaultConfig();

        // Initialiser les gestionnaires
        this.configManager = new ConfigManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.weaponManager = new WeaponManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.questManager = new QuestManager(this, weaponManager);
        this.dungeonManager = new DungeonManager(this);
        this.dungeonGenerator = new DungeonGenerator(this);
        this.dungeonBuilder = new DungeonBuilder(this);
        this.mobManager = new MobManager(this);
        this.menuManager = new MenuManager(this);
        this.economyManager = new EconomyManager(this);
        this.rankManager = new PlayerRankManager();
        this.mobEffectManager = new MobEffectManager(this);
        this.dungeonMobManager = new DungeonMobManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.helpManager = new HelpManager(this);

        // Initialiser les listeners
        this.dungeonListener = new DungeonListener(this);
        this.questListener = new QuestListener(this);
        this.menuListener = new MenuListener(this);
        this.playerListener = new PlayerListener(this);

        // Installer les skins
        this.skinInstaller = new SkinInstaller(this);
        skinInstaller.installSkins();

        // Charger les données
        this.configManager.loadConfig();
        this.playerDataManager.loadAllPlayerData();
        this.questManager.loadQuests();
        this.dungeonManager.loadDungeons();
        this.customItemManager.loadCustomItems();
        this.weaponManager.loadWeapons();
        this.inventoryManager.loadInventories();
        this.helpManager.loadHelpMessages();

        // Enregistrer les listeners
        getServer().getPluginManager().registerEvents(dungeonListener, this);
        getServer().getPluginManager().registerEvents(questListener, this);
        getServer().getPluginManager().registerEvents(menuListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);
        getServer().getPluginManager().registerEvents(new DungeonEventListener(this), this);
        getServer().getPluginManager().registerEvents(new QuestMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopMenuListener(this), this);

        // Enregistrer les commandes
        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("quest").setExecutor(new QuestCommand(this));
        getCommand("profile").setExecutor(new ProfileCommand(this));
        getCommand("class").setExecutor(new ClassCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("exp").setExecutor(new ExpCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("weapon").setExecutor(new WeaponCommand(this));
        getCommand("inventory").setExecutor(new InventoryCommand(this));
        getCommand("config").setExecutor(new ConfigCommand(this));
        getCommand("help").setExecutor(new HelpCommand(this));

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
        if (weaponManager != null) {
            weaponManager.saveWeapons();
        }
        if (inventoryManager != null) {
            inventoryManager.saveInventories();
        }
        if (helpManager != null) {
            helpManager.saveHelpMessages();
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

    public DungeonGenerator getDungeonGenerator() {
        return dungeonGenerator;
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

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public PlayerRankManager getRankManager() {
        return rankManager;
    }

    public MobEffectManager getMobEffectManager() {
        return mobEffectManager;
    }

    public DungeonMobManager getDungeonMobManager() {
        return dungeonMobManager;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }
} 