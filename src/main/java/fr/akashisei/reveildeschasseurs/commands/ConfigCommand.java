package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public ConfigCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reveildeschasseurs.admin")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadConfig(sender);
                break;
            case "save":
                saveConfig(sender);
                break;
            default:
                showHelp(sender);
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Commandes de configuration ===");
        sender.sendMessage(ChatColor.YELLOW + "/config reload " + ChatColor.GRAY + "- Recharge la configuration");
        sender.sendMessage(ChatColor.YELLOW + "/config save " + ChatColor.GRAY + "- Sauvegarde la configuration");
    }

    private void reloadConfig(CommandSender sender) {
        plugin.getConfigManager().loadConfig();
        plugin.getWeaponManager().loadWeapons();
        plugin.getInventoryManager().loadInventories();
        plugin.getHelpManager().loadHelpMessages();
        sender.sendMessage(ChatColor.GREEN + "La configuration a été rechargée.");
    }

    private void saveConfig(CommandSender sender) {
        plugin.getConfigManager().saveConfig();
        plugin.getWeaponManager().saveWeapons();
        plugin.getInventoryManager().saveInventories();
        plugin.getHelpManager().saveHelpMessages();
        sender.sendMessage(ChatColor.GREEN + "La configuration a été sauvegardée.");
    }
} 