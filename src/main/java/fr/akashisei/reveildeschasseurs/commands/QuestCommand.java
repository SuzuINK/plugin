package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public QuestCommand(ReveilDesChasseurs plugin) {
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
            plugin.getMenuManager().openQuestMenu(player, 1);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                plugin.getMenuManager().openQuestMenu(player, 1);
                break;

            case "track":
                // TODO: Implémenter le suivi des quêtes
                player.sendMessage(ChatColor.YELLOW + "Le suivi des quêtes sera bientôt disponible!");
                break;

            case "abandon":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest abandon <nom_de_la_quête>");
                    return true;
                }
                abandonQuest(player, args[1]);
                break;

            case "info":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest info <nom_de_la_quête>");
                    return true;
                }
                showQuestInfo(player, args[1]);
                break;

            case "help":
                sendHelpMessage(player);
                break;

            default:
                player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /quest help pour voir la liste des commandes.");
                break;
        }

        return true;
    }

    private void abandonQuest(Player player, String questName) {
        Quest quest = plugin.getQuestManager().getQuest(questName);
        if (quest == null) {
            player.sendMessage(ChatColor.RED + "Quête introuvable!");
            return;
        }

        if (!plugin.getPlayerDataManager().getOrCreatePlayerData(player).hasActiveQuest(quest.getId())) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas cette quête active!");
            return;
        }

        plugin.getPlayerDataManager().getOrCreatePlayerData(player).removeActiveQuest(quest.getId());
        player.sendMessage(ChatColor.YELLOW + "Vous avez abandonné la quête: " + quest.getName());
    }

    private void showQuestInfo(Player player, String questName) {
        Quest quest = plugin.getQuestManager().getQuest(questName);
        if (quest == null) {
            player.sendMessage(ChatColor.RED + "Quête introuvable!");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== " + quest.getName() + " ===");
        player.sendMessage(ChatColor.WHITE + quest.getDescription());
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Niveau requis: " + ChatColor.WHITE + quest.getRequiredLevel());
        player.sendMessage(ChatColor.YELLOW + "Récompenses:");
        player.sendMessage(ChatColor.WHITE + "- " + quest.getRewardExp() + " XP");
        player.sendMessage(ChatColor.WHITE + "- " + quest.getRewardMoney() + " coins");

        if (!quest.getRequiredQuests().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Quêtes requises:");
            for (String questId : quest.getRequiredQuests()) {
                Quest requiredQuest = plugin.getQuestManager().getQuest(questId);
                if (requiredQuest != null) {
                    player.sendMessage(ChatColor.WHITE + "- " + requiredQuest.getName());
                }
            }
        }

        if (!quest.getRequiredItems().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Items requis:");
            for (String itemId : quest.getRequiredItems()) {
                player.sendMessage(ChatColor.WHITE + "- " + itemId);
            }
        }

        if (!quest.getAllowedClasses().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Classes autorisées:");
            for (String playerClass : quest.getAllowedClasses()) {
                player.sendMessage(ChatColor.WHITE + "- " + playerClass);
            }
        }

        if (!quest.getAllowedRanks().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Rangs autorisés:");
            for (String rank : quest.getAllowedRanks()) {
                player.sendMessage(ChatColor.WHITE + "- " + rank);
            }
        }

        if (quest.isRepeatable()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "Cette quête est répétable");
            if (quest.getMaxCompletions() > 0) {
                player.sendMessage(ChatColor.YELLOW + "Maximum: " + quest.getMaxCompletions() + " fois");
            }
            if (quest.getCooldown() > 0) {
                player.sendMessage(ChatColor.YELLOW + "Cooldown: " + (quest.getCooldown() / 1000) + " secondes");
            }
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Commandes de quêtes ===");
        player.sendMessage(ChatColor.YELLOW + "/quest" + ChatColor.WHITE + " - Ouvre le menu des quêtes");
        player.sendMessage(ChatColor.YELLOW + "/quest list" + ChatColor.WHITE + " - Liste toutes les quêtes disponibles");
        player.sendMessage(ChatColor.YELLOW + "/quest track" + ChatColor.WHITE + " - Active/désactive le suivi des quêtes");
        player.sendMessage(ChatColor.YELLOW + "/quest abandon <nom>" + ChatColor.WHITE + " - Abandonne une quête active");
        player.sendMessage(ChatColor.YELLOW + "/quest info <nom>" + ChatColor.WHITE + " - Affiche les informations d'une quête");
        player.sendMessage(ChatColor.YELLOW + "/quest help" + ChatColor.WHITE + " - Affiche cette aide");
    }
} 