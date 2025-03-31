package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public ProfileCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getMenuManager().openProfileMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats":
                plugin.getMenuManager().openProfileMenu(player);
                break;

            case "reset":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /profile reset <stats|quests>");
                    return true;
                }
                handleReset(player, args[1]);
                break;

            case "help":
                sendHelpMessage(player);
                break;

            default:
                player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /profile help pour voir la liste des commandes.");
                break;
        }

        return true;
    }

    private void handleReset(Player player, String type) {
        switch (type.toLowerCase()) {
            case "stats":
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).setMobsKilled(0);
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).setDeaths(0);
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).setCompletedDungeons(0);
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).setFailedDungeons(0);
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).setCompletedQuests(0);
                plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Vos statistiques ont été réinitialisées!");
                break;

            case "quests":
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).getActiveQuests().clear();
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).getQuestProgress().clear();
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).getQuestCooldowns().clear();
                plugin.getPlayerDataManager().getOrCreatePlayerData(player).getQuestCompletions().clear();
                plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Vos quêtes ont été réinitialisées!");
                break;

            default:
                player.sendMessage(ChatColor.RED + "Type de réinitialisation invalide. Utilisez 'stats' ou 'quests'.");
                break;
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Commandes de profil ===");
        player.sendMessage(ChatColor.YELLOW + "/profile" + ChatColor.WHITE + " - Ouvre le menu de profil");
        player.sendMessage(ChatColor.YELLOW + "/profile stats" + ChatColor.WHITE + " - Affiche vos statistiques");
        player.sendMessage(ChatColor.YELLOW + "/profile reset stats" + ChatColor.WHITE + " - Réinitialise vos statistiques");
        player.sendMessage(ChatColor.YELLOW + "/profile reset quests" + ChatColor.WHITE + " - Réinitialise vos quêtes");
        player.sendMessage(ChatColor.YELLOW + "/profile help" + ChatColor.WHITE + " - Affiche cette aide");
    }
} 