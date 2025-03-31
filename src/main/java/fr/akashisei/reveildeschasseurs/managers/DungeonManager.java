package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.generators.DungeonBuilder;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonGenerator.DungeonTheme;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.GameRule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.*;

public class DungeonManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, Dungeon> dungeons;
    private final Map<UUID, DungeonInstance> activeDungeons;
    private final Map<UUID, String> playerDungeons;
    private final DungeonBuilder dungeonBuilder;

    public DungeonManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.dungeons = new HashMap<>();
        this.activeDungeons = new HashMap<>();
        this.playerDungeons = new HashMap<>();
        this.dungeonBuilder = new DungeonBuilder(plugin);
        loadDungeons();
    }

    public void loadDungeons() {
        dungeons.clear();
        List<String> dungeonIds = plugin.getConfigManager().getDungeonIds();
        
        for (String dungeonId : dungeonIds) {
            File dungeonFile = new File(plugin.getDataFolder(), "dungeons/" + dungeonId + ".yml");
            if (!dungeonFile.exists()) continue;

            YamlConfiguration config = YamlConfiguration.loadConfiguration(dungeonFile);
            Dungeon dungeon = new Dungeon(dungeonId);
            
            // Charger les propriétés de base
            dungeon.setName(config.getString("name", "Donjon sans nom"));
            dungeon.setDescription(config.getString("description", "Aucune description"));
            dungeon.setMinLevel(config.getInt("min_level", 1));
            dungeon.setMaxPlayers(config.getInt("max_players", 4));
            dungeon.setTimeLimit(config.getLong("time_limit", 3600));
            dungeon.setDifficultyMultiplier(config.getDouble("difficulty_multiplier", 1.0));
            
            // Charger les récompenses
            dungeon.setBaseRewardExp(config.getLong("base_reward_exp", 100));
            dungeon.setBaseRewardMoney(config.getLong("base_reward_money", 50));
            dungeon.setRewardItems(config.getStringList("reward_items"));
            
            // Charger les restrictions
            dungeon.setRequiredQuests(config.getStringList("required_quests"));
            dungeon.setRequiredItems(config.getStringList("required_items"));
            dungeon.setForbiddenItems(config.getStringList("forbidden_items"));
            dungeon.setAllowedClasses(config.getStringList("allowed_classes"));
            dungeon.setAllowedRanks(config.getStringList("allowed_ranks"));
            
            // Charger les paramètres de génération
            ConfigurationSection genSection = config.getConfigurationSection("generation");
            if (genSection != null) {
                dungeon.setTheme(DungeonTheme.valueOf(genSection.getString("theme", "STONE")));
                dungeon.setSize(genSection.getInt("size", 3));
                dungeon.setMobDensity(genSection.getDouble("mob_density", 0.5));
                dungeon.setTrapDensity(genSection.getDouble("trap_density", 0.3));
                dungeon.setLootDensity(genSection.getDouble("loot_density", 0.4));
            }
            
            dungeons.put(dungeonId, dungeon);
            plugin.getLogger().info("Donjon chargé : " + dungeon.getName());
        }
    }

    public void saveDungeons() {
        // Sauvegarder les donjons dans la configuration
        // TODO: Implémenter la sauvegarde des donjons
    }

    public Dungeon getDungeon(String id) {
        return dungeons.get(id);
    }

    public List<Dungeon> getAllDungeons() {
        return new ArrayList<>(dungeons.values());
    }

    public boolean createDungeon(Dungeon dungeon) {
        if (dungeons.containsKey(dungeon.getId())) {
            return false;
        }
        dungeons.put(dungeon.getId(), dungeon);
        saveDungeons();
        return true;
    }

    public boolean deleteDungeon(String id) {
        if (!dungeons.containsKey(id)) {
            return false;
        }
        dungeons.remove(id);
        saveDungeons();
        return true;
    }

    public Dungeon getDungeonById(String id) {
        return dungeons.get(id.toLowerCase());
    }

    public List<Dungeon> getAvailableDungeons(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        List<Dungeon> available = new ArrayList<>();
        
        for (Dungeon dungeon : dungeons.values()) {
            if (playerData.getLevel() >= dungeon.getMinLevel()) {
                available.add(dungeon);
            }
        }
        
        return available;
    }

    public void startDungeon(Player player, Dungeon dungeon) {
        if (dungeon == null) {
            player.sendMessage(ChatColor.RED + "Ce donjon n'existe pas !");
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        
        // Vérifier le niveau requis
        if (playerData.getLevel() < dungeon.getMinLevel()) {
            player.sendMessage(ChatColor.RED + "Vous devez être niveau " + dungeon.getMinLevel() + " pour entrer dans ce donjon !");
            return;
        }

        // Vérifier si le joueur est déjà dans un donjon
        if (isPlayerInDungeon(player)) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans un donjon !");
            return;
        }

        // Créer une nouvelle instance de donjon
        Location spawnLocation = dungeon.getSpawnLocation().clone();
        DungeonInstance instance = new DungeonInstance(dungeon, player.getUniqueId(), spawnLocation, player.getLocation());
        
        // Enregistrer l'instance
        activeDungeons.put(player.getUniqueId(), instance);
        playerDungeons.put(player.getUniqueId(), dungeon.getId());

        // Téléporter le joueur
        player.teleport(spawnLocation);
        player.sendMessage(ChatColor.GREEN + "Vous entrez dans le donjon : " + ChatColor.GOLD + dungeon.getName());
    }

    public void endDungeon(Player player, boolean completed) {
        DungeonInstance instance = activeDungeons.get(player.getUniqueId());
        if (instance == null) {
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);

        // Mettre à jour les statistiques
        if (completed) {
            playerData.incrementCompletedDungeons();
            // Donner les récompenses
            Dungeon dungeon = instance.getDungeon();
            playerData.addMoney(dungeon.getBaseRewardMoney());
            playerData.addExperience(dungeon.getBaseRewardExp());
            player.sendMessage(ChatColor.GREEN + "Félicitations ! Vous avez terminé le donjon !");
        } else {
            playerData.incrementFailedDungeons();
            player.sendMessage(ChatColor.RED + "Vous avez échoué le donjon...");
        }

        // Téléporter le joueur à la sortie
        player.teleport(instance.getExitLocation());

        // Nettoyer l'instance
        activeDungeons.remove(player.getUniqueId());
        playerDungeons.remove(player.getUniqueId());
        plugin.getMobManager().removeDungeonMobs(instance.getDungeon().getId());
    }

    public boolean isPlayerInDungeon(Player player) {
        return activeDungeons.containsKey(player.getUniqueId());
    }

    public DungeonInstance getPlayerInstance(Player player) {
        return activeDungeons.get(player.getUniqueId());
    }

    public String getPlayerDungeonId(Player player) {
        return playerDungeons.get(player.getUniqueId());
    }

    public void saveDungeon(Dungeon dungeon) {
        plugin.getConfigManager().saveDungeon(dungeon);
    }

    public boolean canEnterDungeon(Player player, Dungeon dungeon) {
        // Vérifier le niveau requis
        if (plugin.getPlayerDataManager().getPlayerData(player.getUniqueId()).getLevel() < dungeon.getMinLevel()) {
            return false;
        }

        // Vérifier si le joueur a les quêtes requises
        for (String questId : dungeon.getRequiredQuests()) {
            if (!plugin.getQuestManager().hasCompletedQuest(player.getUniqueId(), questId)) {
                return false;
            }
        }

        // Vérifier si le joueur a les items requis
        for (String itemId : dungeon.getRequiredItems()) {
            if (!hasRequiredItem(player, itemId)) {
                return false;
            }
        }

        // Vérifier si le joueur a la classe requise
        if (!dungeon.getAllowedClasses().isEmpty() && 
            !dungeon.getAllowedClasses().contains(plugin.getPlayerDataManager().getPlayerData(player.getUniqueId()).getPlayerClass())) {
            return false;
        }

        // Vérifier si le joueur a le rang requis
        if (!dungeon.getAllowedRanks().isEmpty() && 
            !dungeon.getAllowedRanks().contains(plugin.getPlayerDataManager().getPlayerData(player.getUniqueId()).getRank())) {
            return false;
        }

        return true;
    }

    private boolean hasRequiredItem(Player player, String itemId) {
        // TODO: Implémenter la vérification des items requis
        return true;
    }

    public void saveAll() {
        saveDungeons();
    }

    public DungeonBuilder getDungeonBuilder() {
        return dungeonBuilder;
    }

    public Dungeon getDungeonByName(String name) {
        for (Dungeon dungeon : dungeons.values()) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }
        return null;
    }

    public void removePlayerFromDungeon(Player player) {
        activeDungeons.remove(player.getUniqueId());
        playerDungeons.remove(player.getUniqueId());
    }
} 