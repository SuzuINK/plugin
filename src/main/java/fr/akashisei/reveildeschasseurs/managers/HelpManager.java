package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelpManager {
    private final ReveilDesChasseurs plugin;
    private final File helpFile;
    private final FileConfiguration helpConfig;
    private final Map<String, String> helpMessages;

    public HelpManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.helpFile = new File(plugin.getDataFolder(), "help.yml");
        this.helpConfig = YamlConfiguration.loadConfiguration(helpFile);
        this.helpMessages = new HashMap<>();
        loadHelpMessages();
    }

    public void loadHelpMessages() {
        helpMessages.clear();
        if (!helpFile.exists()) {
            plugin.saveResource("help.yml", false);
        }

        for (String command : helpConfig.getKeys(false)) {
            String message = helpConfig.getString(command);
            if (message != null) {
                helpMessages.put(command, ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public void saveHelpMessages() {
        for (Map.Entry<String, String> entry : helpMessages.entrySet()) {
            helpConfig.set(entry.getKey(), entry.getValue());
        }
        
        try {
            helpConfig.save(helpFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les messages d'aide : " + e.getMessage());
        }
    }

    public void showHelp(Player player, String command) {
        if (command == null || command.isEmpty()) {
            showGeneralHelp(player);
            return;
        }

        String message = helpMessages.get(command.toLowerCase());
        if (message != null) {
            player.sendMessage(message);
        } else {
            player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /help pour voir la liste des commandes disponibles.");
        }
    }

    private void showGeneralHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Aide de ReveilDesChasseurs ===");
        player.sendMessage(ChatColor.YELLOW + "Commandes disponibles :");
        
        for (String command : helpMessages.keySet()) {
            player.sendMessage(ChatColor.GRAY + "- /" + command);
        }
        
        player.sendMessage(ChatColor.YELLOW + "\nPour plus d'informations sur une commande, utilisez :");
        player.sendMessage(ChatColor.GRAY + "/help <commande>");
    }

    public void addHelpMessage(String command, String message) {
        helpMessages.put(command.toLowerCase(), ChatColor.translateAlternateColorCodes('&', message));
        saveHelpMessages();
    }

    public void removeHelpMessage(String command) {
        helpMessages.remove(command.toLowerCase());
        saveHelpMessages();
    }

    public String getHelpMessage(String command) {
        return helpMessages.get(command.toLowerCase());
    }

    public boolean hasHelpMessage(String command) {
        return helpMessages.containsKey(command.toLowerCase());
    }
} 