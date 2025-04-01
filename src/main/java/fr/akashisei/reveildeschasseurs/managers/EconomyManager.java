package fr.akashisei.reveildeschasseurs.managers;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private final Map<UUID, Integer> playerBalances;

    public EconomyManager() {
        this.playerBalances = new HashMap<>();
    }

    public int getBalance(Player player) {
        return playerBalances.getOrDefault(player.getUniqueId(), 0);
    }

    public void setBalance(Player player, int amount) {
        playerBalances.put(player.getUniqueId(), Math.max(0, amount));
    }

    public boolean hasEnough(Player player, int amount) {
        return getBalance(player) >= amount;
    }

    public boolean withdraw(Player player, int amount) {
        if (!hasEnough(player, amount)) {
            return false;
        }
        setBalance(player, getBalance(player) - amount);
        return true;
    }

    public void deposit(Player player, int amount) {
        setBalance(player, getBalance(player) + amount);
    }

    public void loadPlayerData(Player player) {
        // TODO: Charger les données depuis la base de données
        setBalance(player, 0);
    }

    public void savePlayerData(Player player) {
        // TODO: Sauvegarder les données dans la base de données
        int balance = getBalance(player);
        // Implémentation de la sauvegarde
    }
} 