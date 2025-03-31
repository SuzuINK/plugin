package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.Quest;
import fr.akashisei.reveildeschasseurs.menus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RDCCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public RDCCommand(ReveilDesChasseurs plugin) {
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
            plugin.getMenuListener().openMenu(player, new MainMenu(plugin));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "donjons":
                handleDungeons(player);
                break;
            case "quêtes":
                handleQuests(player);
                break;
            case "profil":
                handleProfile(player);
                break;
            case "menu":
                plugin.getMenuListener().openMenu(player, new MainMenu(plugin));
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== ReveilDesChasseurs - Aide ===");
        player.sendMessage(ChatColor.YELLOW + "/rdc donjons " + ChatColor.WHITE + "- Voir les donjons disponibles");
        player.sendMessage(ChatColor.YELLOW + "/rdc quêtes " + ChatColor.WHITE + "- Voir les quêtes disponibles");
        player.sendMessage(ChatColor.YELLOW + "/rdc profil " + ChatColor.WHITE + "- Voir votre profil");
        player.sendMessage(ChatColor.YELLOW + "/rdc menu " + ChatColor.WHITE + "- Ouvrir le menu principal");
    }

    private void handleDungeons(Player player) {
        List<Dungeon> dungeons = plugin.getDungeonManager().getAvailableDungeons(player);
        player.sendMessage(ChatColor.GOLD + "=== Donjons Disponibles ===");
        
        for (Dungeon dungeon : dungeons) {
            player.sendMessage(ChatColor.YELLOW + dungeon.getName() + ChatColor.WHITE + " - " + dungeon.getDescription());
            player.sendMessage(ChatColor.GRAY + "Niveau minimum: " + dungeon.getMinLevel());
            player.sendMessage(ChatColor.GRAY + "Récompense: " + dungeon.getBaseRewardExp() + " XP, " + 
                             dungeon.getBaseRewardMoney() + " coins");
        }
    }

    private void handleQuests(Player player) {
        List<Quest> quests = plugin.getQuestManager().getAvailableQuests(player);
        player.sendMessage(ChatColor.GOLD + "=== Quêtes Disponibles ===");
        
        for (Quest quest : quests) {
            player.sendMessage(ChatColor.YELLOW + quest.getName() + ChatColor.WHITE + " - " + quest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Niveau minimum: " + quest.getMinLevel());
            player.sendMessage(ChatColor.GRAY + "Récompense: " + quest.getRewardExp() + " XP, " + 
                             quest.getRewardMoney() + " coins");
        }
    }

    private void handleProfile(Player player) {
        var playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        player.sendMessage(ChatColor.GOLD + "=== Votre Profil ===");
        player.sendMessage(ChatColor.YELLOW + "Nom: " + ChatColor.WHITE + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Rang: " + ChatColor.WHITE + playerData.getRank());
        player.sendMessage(ChatColor.YELLOW + "Niveau: " + ChatColor.WHITE + playerData.getLevel());
        player.sendMessage(ChatColor.YELLOW + "XP: " + ChatColor.WHITE + playerData.getExperience());
        player.sendMessage(ChatColor.YELLOW + "Donjons complétés: " + ChatColor.WHITE + playerData.getCompletedDungeons());
        player.sendMessage(ChatColor.YELLOW + "Quêtes complétées: " + ChatColor.WHITE + playerData.getCompletedQuests());
    }
} 