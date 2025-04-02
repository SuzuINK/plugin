package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private final ReveilDesChasseurs plugin;
    private final Map<UUID, Double> playerMoney;

    public EconomyManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.playerMoney = new HashMap<>();
        loadMoney();
    }

    public void loadMoney() {
        // Charger l'argent des joueurs depuis la configuration
        // À implémenter selon les besoins
    }

    public void saveMoney() {
        // Sauvegarder l'argent des joueurs dans la configuration
        // À implémenter selon les besoins
    }

    public double getMoney(Player player) {
        return playerMoney.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void setMoney(Player player, double amount) {
        playerMoney.put(player.getUniqueId(), amount);
        saveMoney();
    }

    public void addMoney(Player player, double amount) {
        double currentMoney = getMoney(player);
        setMoney(player, currentMoney + amount);
    }

    public void removeMoney(Player player, double amount) {
        double currentMoney = getMoney(player);
        if (currentMoney >= amount) {
            setMoney(player, currentMoney - amount);
        }
    }

    public boolean hasMoney(Player player, double amount) {
        return getMoney(player) >= amount;
    }

    public void loadPlayerData(Player player) {
        // TODO: Charger les données depuis la base de données
        setMoney(player, 0.0);
    }

    public void savePlayerData(Player player) {
        // TODO: Sauvegarder les données dans la base de données
        double balance = getMoney(player);
        // Implémentation de la sauvegarde
    }

    public void depositPlayer(Player player, double amount) {
        // TODO: Implémenter la logique pour ajouter de l'argent au joueur
        // Pour l'instant, on utilise un message de débogage
        player.sendMessage("§a+" + amount + " pièces");
    }

    public double getBalance(Player player) {
        return getMoney(player);
    }

    public boolean hasEnough(Player player, int amount) {
        return hasMoney(player, amount);
    }

    public boolean withdraw(Player player, int amount) {
        if (hasEnough(player, amount)) {
            removeMoney(player, amount);
            return true;
        }
        return false;
    }
} 