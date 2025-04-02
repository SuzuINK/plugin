package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.menus.QuestMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class QuestCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;
    private QuestMenu questMenu;

    public QuestCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    private void openQuestMenu(Player player) {
        List<Quest> quests = plugin.getQuestManager().getAvailableQuests(player);
        questMenu = new QuestMenu(plugin, player, quests);
        questMenu.open(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("reveildeschasseurs.quest")) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        if (args.length == 0) {
            openQuestMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "track":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest track <nom_quete>");
                    return true;
                }
                trackQuest(player, args[1]);
                break;
            case "untrack":
                untrackQuest(player);
                break;
            case "toggle":
                toggleQuestTracking(player);
                break;
            case "abandon":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest abandon <nom_quete>");
                    return true;
                }
                abandonQuest(player, args[1]);
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /quest info <nom_quete>");
                    return true;
                }
                showQuestInfo(player, args[1]);
                break;
            case "list":
                questMenu.openAvailableQuests(player);
                break;
            case "active":
                questMenu.openActiveQuests(player);
                break;
            case "completed":
                questMenu.openCompletedQuests(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Aide des Quêtes ===");
        player.sendMessage(ChatColor.YELLOW + "/quest" + ChatColor.WHITE + " - Ouvre le menu des quêtes");
        player.sendMessage(ChatColor.YELLOW + "/quest list" + ChatColor.WHITE + " - Affiche les quêtes disponibles");
        player.sendMessage(ChatColor.YELLOW + "/quest active" + ChatColor.WHITE + " - Affiche vos quêtes actives");
        player.sendMessage(ChatColor.YELLOW + "/quest completed" + ChatColor.WHITE + " - Affiche vos quêtes complétées");
        player.sendMessage(ChatColor.YELLOW + "/quest info <quête>" + ChatColor.WHITE + " - Affiche les détails d'une quête");
        player.sendMessage(ChatColor.YELLOW + "/quest track <quête>" + ChatColor.WHITE + " - Suit une quête");
        player.sendMessage(ChatColor.YELLOW + "/quest untrack" + ChatColor.WHITE + " - Arrête de suivre la quête actuelle");
        player.sendMessage(ChatColor.YELLOW + "/quest toggle" + ChatColor.WHITE + " - Active/désactive le suivi des quêtes");
        player.sendMessage(ChatColor.YELLOW + "/quest abandon <quête>" + ChatColor.WHITE + " - Abandonne une quête");
    }

    private void trackQuest(Player player, String questId) {
        plugin.getQuestManager().trackQuest(player, questId);
    }

    private void untrackQuest(Player player) {
        plugin.getQuestManager().untrackQuest(player);
    }

    private void toggleQuestTracking(Player player) {
        plugin.getQuestManager().toggleQuestTracking(player);
    }

    private void abandonQuest(Player player, String questId) {
        Quest quest = plugin.getQuestManager().getQuest(questId);
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

    private void showQuestInfo(Player player, String questId) {
        Quest quest = plugin.getQuestManager().getQuest(questId);
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
            for (String reqQuestId : quest.getRequiredQuests()) {
                Quest requiredQuest = plugin.getQuestManager().getQuest(reqQuestId);
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