package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.generators.DungeonBuilder;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.GameRule;
import org.bukkit.ChatColor;

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

    private void loadDungeons() {
        // Charger les donjons depuis la configuration
        dungeons.clear();
        // TODO: Implémenter le chargement des donjons
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

    public void startDungeon(Player player, String dungeonId) {
        Dungeon dungeon = getDungeonById(dungeonId);
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
        playerDungeons.put(player.getUniqueId(), dungeonId);

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

    public DungeonInstance getPlayerDungeonInstance(Player player) {
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
} 