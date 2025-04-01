package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
            player.sendMessage(ChatColor.GOLD + "=== Commandes de quête ===");
            player.sendMessage(ChatColor.YELLOW + "/quest list" + ChatColor.WHITE + " - Voir la liste des quêtes disponibles");
            player.sendMessage(ChatColor.YELLOW + "/quest active" + ChatColor.WHITE + " - Voir vos quêtes actives");
            player.sendMessage(ChatColor.YELLOW + "/quest accept <id>" + ChatColor.WHITE + " - Accepter une quête");
            player.sendMessage(ChatColor.YELLOW + "/quest abandon <id>" + ChatColor.WHITE + " - Abandonner une quête");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                // TODO: Implémenter la liste des quêtes disponibles
                player.sendMessage(ChatColor.GOLD + "=== Quêtes disponibles ===");
                player.sendMessage(ChatColor.WHITE + "- Chasseur de Monstres (Niveau 1)");
                player.sendMessage(ChatColor.WHITE + "- Explorateur de Donjons (Niveau 2)");
                player.sendMessage(ChatColor.WHITE + "- Maître des Ombres (Niveau 3)");
                break;

            case "active":
                // TODO: Implémenter la liste des quêtes actives
                player.sendMessage(ChatColor.GOLD + "=== Vos quêtes actives ===");
                player.sendMessage(ChatColor.WHITE + "Aucune quête active");
                break;

            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest accept <id>");
                    return true;
                }
                // TODO: Implémenter l'acceptation d'une quête
                player.sendMessage(ChatColor.GREEN + "Quête acceptée !");
                break;

            case "abandon":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest abandon <id>");
                    return true;
                }
                // TODO: Implémenter l'abandon d'une quête
                player.sendMessage(ChatColor.RED + "Quête abandonnée.");
                break;

            default:
                player.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez /quest pour voir les commandes disponibles.");
                break;
        }

        return true;
    }

    private void toggleQuestTracking(Player player) {
        var playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        boolean isTracking = playerData.isQuestTracking();
        
        if (isTracking) {
            playerData.setQuestTracking(false);
            player.sendMessage(ChatColor.YELLOW + "Le suivi des quêtes a été désactivé.");
        } else {
            playerData.setQuestTracking(true);
            player.sendMessage(ChatColor.GREEN + "Le suivi des quêtes a été activé.");
            // Afficher la quête suivie actuelle
            String trackedQuestId = playerData.getTrackedQuest();
            if (trackedQuestId != null) {
                Quest quest = plugin.getQuestManager().getQuest(trackedQuestId);
                if (quest != null) {
                    showTrackedQuestInfo(player, quest);
                }
            }
        }
    }

    private void showTrackedQuestInfo(Player player, Quest quest) {
        var playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        player.sendMessage(ChatColor.GOLD + "=== Quête suivie ===");
        player.sendMessage(ChatColor.WHITE + quest.getName());
        player.sendMessage(ChatColor.WHITE + quest.getDescription());
        
        // Afficher la progression
        Map<String, Integer> objectives = quest.getObjectives();
        for (Map.Entry<String, Integer> entry : objectives.entrySet()) {
            String objective = entry.getKey();
            int required = entry.getValue();
            int current = playerData.getQuestProgress(quest.getId(), objective);
            player.sendMessage(ChatColor.YELLOW + objective + ": " + 
                ChatColor.WHITE + current + "/" + required);
        }
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
            for (Map.Entry<ItemStack, Integer> entry : quest.getRequiredItems().entrySet()) {
                ItemStack item = entry.getKey();
                int amount = entry.getValue();
                String itemName = item.getItemMeta().hasDisplayName() ? 
                    item.getItemMeta().getDisplayName() : 
                    item.getType().name();
                player.sendMessage(ChatColor.WHITE + "- " + itemName + " x" + amount);
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
} 